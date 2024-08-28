import React, { JSXElementConstructor, ReactNode } from 'react';
import { Box, List, Paper, PaperProps } from '@mui/material';
import { Pagination } from '@mui/material';

export type OverridableListWrappers<
  WrapperComponent extends
    | keyof JSX.IntrinsicElements
    | JSXElementConstructor<any>,
  ListComponent extends keyof JSX.IntrinsicElements | JSXElementConstructor<any>
> = {
  wrapperComponent?: WrapperComponent;
  wrapperComponentProps?: WrapperComponent extends typeof Paper
    ? React.ComponentProps<typeof Paper>
    : React.ComponentProps<WrapperComponent>;
  listComponent?: ListComponent;
  listComponentProps?: ListComponent extends typeof List
    ? React.ComponentProps<typeof List>
    : React.ComponentProps<ListComponent>;
};

export const SimpleList = <
  DataItem,
  WrapperComponent extends
    | keyof JSX.IntrinsicElements
    | JSXElementConstructor<any>,
  ListComponent extends keyof JSX.IntrinsicElements | JSXElementConstructor<any>
>(
  props: {
    data: DataItem[];
    pagination?: {
      page: number;
      pageCount: number;
      onPageChange: (page: number) => void;
    };
    renderItem: (item: DataItem) => ReactNode;
  } & OverridableListWrappers<WrapperComponent, ListComponent>
) => {
  const { data, pagination } = props;

  const Wrapper = props.wrapperComponent || Paper;
  const baseProps =
    Wrapper === Paper ? ({ variant: 'outlined' } as PaperProps) : {};
  const wrapperProps = { ...baseProps, ...props.wrapperComponentProps };
  const ListWrapper = props.listComponent || List;

  return (
    <>
      <Wrapper {...wrapperProps}>
        <ListWrapper data-cy="global-list-items" {...props.listComponentProps}>
          {data.map((item, index) => (
            <React.Fragment key={(item as any).id || index}>
              {props.renderItem(item)}
            </React.Fragment>
          ))}
        </ListWrapper>
        {pagination && pagination.pageCount > 1 && (
          <Box display="flex" justifyContent="flex-end" mt={1} mb={1}>
            <Pagination
              data-cy="global-list-pagination"
              page={pagination.page}
              count={pagination.pageCount}
              onChange={(_, value) => props.pagination?.onPageChange(value)}
            />
          </Box>
        )}
      </Wrapper>
    </>
  );
};
