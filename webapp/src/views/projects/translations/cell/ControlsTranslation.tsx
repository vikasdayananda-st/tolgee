import React from 'react';
import clsx from 'clsx';
import { Badge, Box, styled } from '@mui/material';
import { Check, Comment, Edit } from '@mui/icons-material';
import { T } from '@tolgee/react';

import { StateInType } from 'tg.constants/translationStates';
import { components } from 'tg.service/apiSchema.generated';
import { ControlsButton } from './ControlsButton';
import { StateTransitionButtons } from './StateTransitionButtons';
import { CELL_HIGHLIGHT_ON_HOVER, CELL_SHOW_ON_HOVER } from './styles';

type State = components['schemas']['TranslationViewModel']['state'];

const StyledControlsWrapper = styled(Box)`
  display: grid;
  box-sizing: border-box;
  justify-content: end;
  padding: 0px 0px 0px 0px;
  gap: 4px;
  margin: 0px 0px;
`;

const StyledStateButtons = styled('div')`
  display: flex;
  justify-content: flex-end;
  padding-right: 8px;
`;

const StyledBadge = styled(Badge)`
  & .unresolved {
    font-size: 10px;
    height: unset;
    padding: 3px 3px;
    display: flex;
  }
  & .resolved {
    background: ${({ theme }) => theme.palette.emphasis[600]};
    padding: 0px;
    height: 16px;
    width: 18px;
    display: flex;
    min-width: unset;
    align-items: center;
    justify-content: center;
  }
`;

const StyledCheckIcon = styled(Check)`
  color: ${({ theme }) => theme.palette.emphasis[100]};
  font-size: 14px;
  margin: -5px;
`;

type ControlsProps = {
  state?: State;
  editEnabled?: boolean;
  stateChangeEnabled?: boolean;
  onEdit?: () => void;
  onStateChange?: (state: StateInType) => void;
  onComments?: () => void;
  commentsCount: number | undefined;
  unresolvedCommentCount: number | undefined;
  // render last focusable button
  lastFocusable: boolean;
  active?: boolean;
  containerProps?: React.ComponentProps<typeof Box>;
  className?: string;
};

export const ControlsTranslation: React.FC<ControlsProps> = ({
  state,
  editEnabled,
  stateChangeEnabled,
  onEdit,
  onStateChange,
  onComments,
  commentsCount,
  unresolvedCommentCount,
  lastFocusable,
  active,
  className,
}) => {
  const spots: string[] = [];

  const displayTransitionButtons = stateChangeEnabled && state;
  const displayEdit = editEnabled && onEdit;
  const commentsPresent = Boolean(commentsCount);
  const displayComments = onComments || commentsPresent;
  const onlyResolved = commentsPresent && !unresolvedCommentCount;

  if (displayTransitionButtons) {
    spots.push('state');
  }
  if (displayEdit) {
    spots.push('edit');
  }
  if (displayComments) {
    spots.push('comments');
  }

  const inDomTransitionButtons = displayTransitionButtons && active;
  const inDomEdit = displayEdit && active;
  const inDomComments = displayComments || active || lastFocusable;

  const gridTemplateAreas = `'${spots.join(' ')}'`;
  const gridTemplateColumns = spots
    .map((spot) => (spot === 'state' ? 'auto' : '28px'))
    .join(' ');

  return (
    <StyledControlsWrapper
      style={{
        gridTemplateAreas,
        gridTemplateColumns,
      }}
      className={className}
    >
      {inDomTransitionButtons && (
        <StyledStateButtons style={{ gridArea: 'state' }}>
          <StateTransitionButtons
            state={state}
            onStateChange={onStateChange}
            className={CELL_SHOW_ON_HOVER}
          />
        </StyledStateButtons>
      )}
      {inDomEdit && (
        <ControlsButton
          style={{ gridArea: 'edit' }}
          onClick={onEdit}
          data-cy="translations-cell-edit-button"
          className={CELL_SHOW_ON_HOVER}
          tooltip={<T keyName="translations_cell_edit" />}
        >
          <Edit fontSize="small" />
        </ControlsButton>
      )}
      {inDomComments && (
        <ControlsButton
          style={{ gridArea: 'comments' }}
          onClick={onComments}
          data-cy="translations-cell-comments-button"
          className={clsx({
            [CELL_SHOW_ON_HOVER]: !commentsPresent,
            [CELL_HIGHLIGHT_ON_HOVER]: onlyResolved,
          })}
          tooltip={<T keyName="translation_cell_comments" />}
        >
          {onlyResolved ? (
            <StyledBadge
              badgeContent={<StyledCheckIcon fontSize="small" />}
              classes={{
                badge: 'resolved',
              }}
            >
              <Comment fontSize="small" />
            </StyledBadge>
          ) : (
            <StyledBadge
              badgeContent={unresolvedCommentCount}
              color="primary"
              classes={{ badge: 'unresolved' }}
            >
              <Comment fontSize="small" />
            </StyledBadge>
          )}
        </ControlsButton>
      )}
    </StyledControlsWrapper>
  );
};
