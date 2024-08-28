import { styled } from '@mui/material';
import { T } from '@tolgee/react';
import { Check, Clear } from '@mui/icons-material';

import { DiffValue } from '../types';

const StyledDiff = styled('span')`
  display: inline-flex;
  align-items: center;
  gap: 3px;
  position: relative;
  &.removed::after {
    content: '';
    pointer-events: none;
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    background: ${({ theme }) => theme.palette.text.primary};
    height: 1px;
  }
`;

const StyledResolved = styled(StyledDiff)`
  color: ${({ theme }) => theme.palette.activity.added};
`;

const StyledNeedsResolution = styled(StyledDiff)`
  color: ${({ theme }) => theme.palette.activity.removed};
`;

export const getValue = (value: string, type: 'removed' | 'added') => {
  if (value === 'RESOLVED') {
    return (
      <StyledResolved className={type}>
        <Check fontSize="small" />
        <div>
          <T keyName="translations_comments_resolved" />
        </div>
      </StyledResolved>
    );
  } else {
    return (
      <StyledNeedsResolution className={type}>
        <Clear fontSize="small" />
        <div>
          <T keyName="translations_comments_needs_resolution" />
        </div>
      </StyledNeedsResolution>
    );
  }
};

export const getCommentStateChange = (input?: DiffValue) => {
  const oldInput = input?.old;
  const newInput = input?.new;

  if (newInput) {
    return getValue(newInput, 'added');
  } else if (oldInput) {
    return getValue(oldInput, 'removed');
  }
};
