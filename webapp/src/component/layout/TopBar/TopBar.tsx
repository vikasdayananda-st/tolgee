import { Link } from 'react-router-dom';
import { Box, styled, useTheme } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';

import { useGlobalContext } from 'tg.globalContext/GlobalContext';
import {
  useConfig,
  useIsEmailVerified,
  useUser,
} from 'tg.globalContext/helpers';
import { TolgeeLogo } from 'tg.component/common/icons/TolgeeLogo';

import { UserMenu } from '../../security/UserMenu/UserMenu';
import { AdminInfo } from './AdminInfo';
import { QuickStartTopBarButton } from '../QuickStartGuide/QuickStartTopBarButton';
import { LanguageMenu } from 'tg.component/layout/TopBar/LanguageMenu';

export const TOP_BAR_HEIGHT = 52;

export const StyledAppBar = styled(AppBar)(
  ({ theme }) =>
    ({
      zIndex: theme.zIndex.drawer + 1,
      transition: 'transform 0.2s ease-in-out',
      ...theme.mixins.toolbar,
      background: theme.palette.navbar.background,
      color: theme.palette.text.primary,
      boxShadow:
        theme.palette.mode === 'light'
          ? `0px 4px 6px 0px rgba(0, 0, 0, 0.02);`
          : 'none',
    } as any)
);

const StyledToolbar = styled(Toolbar)`
  padding-right: 12.5px !important;
  padding-left: 12.5px !important;
`;

const StyledVersion = styled(Typography)`
  margin-left: ${({ theme }) => theme.spacing(2)};
  font-size: 11px;
`;

const StyledLogoTitle = styled(Typography)`
  font-size: 20px;
  font-weight: 500;
  font-family: Righteous, Rubik, Arial, sans-serif;
  transition: filter 0.2s ease-in-out;
`;

const StyledLogoWrapper = styled(Box)`
  transition: filter 0.2s ease-in-out;
`;

const StyledTolgeeLink = styled(Link)`
  color: ${({ theme }) => theme.palette.navbar.text};
  text-decoration: inherit;
  outline: 0;

  &:focus .logoWrapper {
    filter: brightness(95%);
  }
`;

type Props = {
  isAdminAccess?: boolean;
  isDebuggingCustomerAccount?: boolean;
};

export const TopBar: React.FC<Props> = ({
  isAdminAccess = false,
  isDebuggingCustomerAccount = false,
}) => {
  const config = useConfig();

  const topBarHidden = useGlobalContext((c) => !c.layout.topBarHeight);
  const topBannerSize = useGlobalContext((c) => c.layout.topBannerHeight);
  const user = useUser();

  const theme = useTheme();
  const isEmailVerified = useIsEmailVerified();

  return (
    <StyledAppBar
      sx={{
        top: topBannerSize,
        transform: topBarHidden
          ? `translate(0px, -55px)`
          : `translate(0px, 0px)`,
      }}
    >
      <StyledToolbar>
        <Box flexGrow={1} display="flex">
          <Box>
            <StyledTolgeeLink to={'/'}>
              <Box display="flex" alignItems="center">
                <StyledLogoWrapper
                  pr={1}
                  display="flex"
                  justifyItems="center"
                  className="logoWrapper"
                >
                  <TolgeeLogo
                    fontSize="large"
                    sx={{ color: theme.palette.navbar.logo }}
                  />
                </StyledLogoWrapper>
                <StyledLogoTitle variant="h5" color="inherit">
                  {config.appName}
                </StyledLogoTitle>
                {config.showVersion && (
                  <StyledVersion variant="body1">
                    {config.version}
                  </StyledVersion>
                )}
              </Box>
            </StyledTolgeeLink>
          </Box>
          <AdminInfo
            adminAccess={isAdminAccess}
            debuggingCustomerAccount={isDebuggingCustomerAccount}
          />
        </Box>
        {isEmailVerified && <QuickStartTopBarButton />}
        {!user && <LanguageMenu />}
        {user && <UserMenu />}
      </StyledToolbar>
    </StyledAppBar>
  );
};
