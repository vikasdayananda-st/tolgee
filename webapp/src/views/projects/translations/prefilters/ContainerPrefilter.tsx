import { FilterList } from '@mui/icons-material';
import { Button, styled, useMediaQuery } from '@mui/material';
import { T } from '@tolgee/react';

import { usePrefilter } from './usePrefilter';
import { useGlobalContext } from 'tg.globalContext/GlobalContext';

const StyledContainer = styled('div')`
  margin-top: -4px;
  margin-bottom: 12px;
  background: ${({ theme }) => theme.palette.revisionFilterBanner.background};
  padding: 0px 4px 0px 14px;
  border-radius: 4px;
  height: 40px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  max-width: 100%;
  align-items: center;
`;

const StyledLabel = styled('div')`
  color: ${({ theme }) => theme.palette.revisionFilterBanner.highlightText};
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: 16px;
  flex-shrink: 1;
  overflow: hidden;
`;

const StyledLabelText = styled('div')`
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
`;

const StyledClear = styled('div')`
  flex-grow: 1;
  display: flex;
  justify-content: flex-end;
  white-space: nowrap;
`;

type Props = {
  title: React.ReactNode;
  content: React.ReactNode;
};

export const PrefilterContainer = ({ content, title }: Props) => {
  const { clear } = usePrefilter();

  const rightPanelWidth = useGlobalContext((c) => c.layout.rightPanelWidth);
  const isSmall = useMediaQuery(
    `@media(max-width: ${rightPanelWidth + 1000}px)`
  );

  return (
    <StyledContainer>
      <StyledLabel>
        <FilterList color="inherit" />
        <StyledLabelText>{title}</StyledLabelText>
      </StyledLabel>
      {!isSmall && content}
      <StyledClear>
        <Button size="small" onClick={clear} color="inherit">
          <T keyName="activity_filter_indicator_clear" />
        </Button>
      </StyledClear>
    </StyledContainer>
  );
};
