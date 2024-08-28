import { useGlobalContext } from 'tg.globalContext/GlobalContext';
import { useEffect } from 'react';
import * as Sentry from '@sentry/browser';
import { useGlobalLoading } from './GlobalLoading';
import { PostHog } from 'posthog-js';
import { getUtmParams } from 'tg.fixtures/utmCookie';
import { useIdentify } from 'tg.hooks/useIdentify';
import { useIsFetching, useIsMutating } from 'react-query';
import { useConfig, useUser } from 'tg.globalContext/helpers';

const POSTHOG_INITIALIZED_WINDOW_PROPERTY = 'postHogInitialized';
export const MandatoryDataProvider = (props: any) => {
  const userData = useUser();
  const config = useConfig();

  const isFetching = useGlobalContext((c) => c.initialData.isFetching);

  const isGloballyFetching = useIsFetching();
  const isGloballyMutating = useIsMutating();

  useGlobalLoading(
    Boolean(isGloballyFetching || isGloballyMutating || isFetching)
  );

  useIdentify(userData?.id);

  useEffect(() => {
    if (config?.clientSentryDsn) {
      Sentry.init({
        dsn: config.clientSentryDsn,
        replaysSessionSampleRate: 1.0,
        replaysOnErrorSampleRate: 1.0,
      });
      // eslint-disable-next-line no-console
      console.info('Using Sentry!');
    }
  }, [config?.clientSentryDsn]);

  function initPostHog() {
    let postHogPromise: Promise<PostHog> | undefined;
    if (!window[POSTHOG_INITIALIZED_WINDOW_PROPERTY]) {
      const postHogAPIKey = config?.postHogApiKey;
      if (postHogAPIKey) {
        window[POSTHOG_INITIALIZED_WINDOW_PROPERTY] = true;
        postHogPromise = import('posthog-js').then((m) => m.default);
        postHogPromise.then((posthog) => {
          posthog.init(postHogAPIKey, {
            api_host: config?.postHogHost || undefined,
          });
          if (userData) {
            posthog.identify(userData.id.toString(), {
              name: userData.username,
              email: userData.username,
              ...getUtmParams(),
            });
          }
        });
      }
    }
    return () => {
      postHogPromise?.then((ph) => {
        ph.reset();
      });
      window[POSTHOG_INITIALIZED_WINDOW_PROPERTY] = false;
    };
  }

  useEffect(() => {
    return initPostHog();
  }, [userData?.id, config?.postHogApiKey]);

  useEffect(() => {
    if (userData) {
      Sentry.setUser({
        email: userData.username,
        id: userData.id.toString(),
      });
    }
  }, [userData?.id]);

  return props.children;
};
