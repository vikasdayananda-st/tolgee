import { Typography, Dialog, useMediaQuery, styled } from '@mui/material';
import { T } from '@tolgee/react';

import { useUrlSearchState } from 'tg.hooks/useUrlSearchState';
import { useBottomPanel } from 'tg.component/bottomPanel/BottomPanelContext';

import {
  useTranslationsActions,
  useTranslationsSelector,
} from '../context/TranslationsContext';
import { KeyCreateDialog } from './KeyCreateDialog';
import { TranslationControls } from './TranslationControls';
import { TranslationControlsCompact } from './TranslationControlsCompact';
import { useState } from 'react';
import { confirmation } from 'tg.hooks/confirmation';
import { useGlobalContext } from 'tg.globalContext/GlobalContext';
import { SelectAllCheckbox } from '../BatchOperations/SelectAllCheckbox';
import { fetchTranslationMultiple } from '../TranslationsTable/TranslationWrite';

const StyledResultCount = styled('div')`
  padding: 9px 0px 4px 0px;
  margin-left: 15px;
  display: flex;
  align-items: center;
  gap: 13px;
`;

const StyledDialog = styled(Dialog)`
  transition: 'margin-bottom 0.2s ease-in-out';
`;

export const TranslationsHeader = () => {
  const [newCreateDialog, setNewCreateDialog] = useUrlSearchState('create', {
    defaultVal: 'false',
  });
  const { height: bottomPanelHeight } = useBottomPanel();
  const rightPanelWidth = useGlobalContext((c) => c.layout.rightPanelWidth);
  const [dirty, setDirty] = useState(false);

  const onDialogOpen = () => {
    setNewCreateDialog('true');
  };

  const onDialogClose = () => {
    setNewCreateDialog('false');
  };

  const isSmall = useMediaQuery(
    `@media(max-width: ${rightPanelWidth + 1000}px)`
  );

  const translationsTotal = useTranslationsSelector((c) => c.translationsTotal);

  const dataReady = useTranslationsSelector((c) => c.dataReady);

  function closeGracefully() {
    if (dirty) {
      confirmation({
        message: <T keyName="translations_new_key_discard_message" />,
        confirmButtonText: <T keyName="translations_new_key_discard_button" />,
        onConfirm: onDialogClose,
      });
    } else {
      onDialogClose();
    }
  }

  const selection = useTranslationsSelector((c) => c.selection);
  const translations = useTranslationsSelector((c) => c.translations);
  const languages = useTranslationsSelector((c) => c.languages);
  const selectedLocales =
    translations?.filter((locale) => selection.includes(locale.keyId)) || [];
  const selectedLocalesKeyAndText = selectedLocales.map((locale) => ({
    keyId: locale.keyId,
    text: locale.translations.en.text,
  }));

  const { updateTranslation } = useTranslationsActions();

  const translateAll = async (languageTag: string) => {
    const translatedLocales = await fetchTranslationMultiple({
      queryKey: ['multiple', languageTag, selectedLocalesKeyAndText],
    });
    selectedLocales.forEach((locale) => {
      updateTranslation({
        keyId: locale.keyId,
        lang: languageTag,
        data: {
          auto: false,
          text: translatedLocales.find((f) => f.keyId === locale.keyId).text,
          state: 'TRANSLATED',
        },
      });
    });
  };

  const handleTranslateAll = async () => {
    if (!languages) return;
    const translationPromises = languages.map((language) =>
      translateAll(language.tag)
    );
    await Promise.all(translationPromises);
  };

  return (
    <>
      {isSmall ? (
        <TranslationControlsCompact onDialogOpen={onDialogOpen} />
      ) : (
        <TranslationControls onDialogOpen={onDialogOpen} />
      )}
      {dataReady && translationsTotal ? (
        <StyledResultCount>
          <SelectAllCheckbox />
          <Typography
            color="textSecondary"
            variant="body2"
            data-cy="translations-key-count"
          >
            <T
              keyName="translations_results_count"
              params={{ count: String(translationsTotal) }}
            />
          </Typography>
          <button
            style={{
              background: 'transparent',
              color: '#f86793',
              border: '1px solid #f86793',
              borderRadius: '3px',
              fontWeight: 'bold',
            }}
            onClick={handleTranslateAll}
          >
            🤖 AI Translate
          </button>
        </StyledResultCount>
      ) : null}
      {dataReady && newCreateDialog === 'true' && (
        <StyledDialog
          open={true}
          onClose={closeGracefully}
          fullWidth
          maxWidth="md"
          keepMounted={false}
          style={{ marginBottom: bottomPanelHeight }}
        >
          <KeyCreateDialog onClose={onDialogClose} onDirtyChange={setDirty} />
        </StyledDialog>
      )}
    </>
  );
};
