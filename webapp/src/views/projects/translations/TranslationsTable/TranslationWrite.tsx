import { useRef, useState } from 'react';
import { EditorView } from 'codemirror';
import { styled } from '@mui/material';
import { Placeholder } from '@tginternal/editor';
import { useQuery } from 'react-query';

import { ControlsEditorMain } from '../cell/ControlsEditorMain';
import { ControlsEditorSmall } from '../cell/ControlsEditorSmall';
import { useTranslationsSelector } from '../context/TranslationsContext';
import { useTranslationCell } from '../useTranslationCell';
import { TranslationEditor } from '../TranslationEditor';
import { MissingPlaceholders } from '../cell/MissingPlaceholders';
import { useMissingPlaceholders } from '../cell/useMissingPlaceholders';
import { TranslationVisual } from '../translationVisual/TranslationVisual';
import { ControlsEditorReadOnly } from '../cell/ControlsEditorReadOnly';
import { useBaseTranslation } from '../useBaseTranslation';
import { translateStrings } from 'tg.hooks/useTranslate';

const StyledContainer = styled('div')`
  display: grid;
`;

const StyledEditor = styled('div')`
  padding: 12px 12px 12px 16px;
`;

const StyledBottom = styled('div')`
  display: flex;
  padding: 4px 12px 12px 16px;
  flex-wrap: wrap;
  gap: 14px;
  align-items: center;
`;

const StyledControls = styled('div')`
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  flex-grow: 1;
`;

type Props = {
  tools: ReturnType<typeof useTranslationCell>;
};

const fetchTranslation = async ({ queryKey }) => {
  const [, languageName, translationText] = queryKey;

  // Call the translateStrings function and return the result
  const prompt = `Translate the following text to ${languageName} and provide the response as a JSON object with a key "translatedText":
  "${translationText}"`;

  const response = await translateStrings({
    prompt,
    model: 'gpt-4o-mini',
    temperature: 0.7,
    team: 'staywise',
  });

  return response;
};

export const fetchTranslationMultiple = async ({ queryKey }) => {
  const [, languageName, selectedLocalesKeyAndText] = queryKey;

  // Call the translateStrings function and return the result
  const prompt = `Translate the following strings to ${languageName} and provide the response as an array with a key "translatedText":
  ${JSON.stringify(selectedLocalesKeyAndText, null, 2)}`;

  const response = await translateStrings({
    prompt,
    model: 'gpt-4o-mini',
    temperature: 0.7,
    team: 'staywise',
  });
  return response.translatedText;
};

export const TranslationWrite: React.FC<Props> = ({ tools }) => {
  const {
    value,
    keyData,
    translation,
    language,
    canChangeState,
    setState,
    handleSave,
    handleClose,
    handleInsertBase,
    editEnabled,
    disabled,
  } = tools;
  const editVal = tools.editVal!;
  const state = translation?.state || 'UNTRANSLATED';
  const activeVariant = editVal.activeVariant;

  const [mode, setMode] = useState<'placeholders' | 'syntax'>('placeholders');
  const editorRef = useRef<EditorView>(null);
  const baseLanguage = useTranslationsSelector((c) => c.baseLanguage);
  const nested = Boolean(editVal.value.parameter);

  const baseTranslation = useBaseTranslation(
    activeVariant,
    keyData.translations[baseLanguage]?.text,
    keyData.keyIsPlural
  );

  const missingPlaceholders = useMissingPlaceholders({
    baseTranslation,
    currentTranslation: value,
    nested,
    enabled: baseLanguage !== language.tag,
  });

  const handleModeToggle = () => {
    setMode((mode) => (mode === 'syntax' ? 'placeholders' : 'syntax'));
  };

  const handlePlaceholderClick = (placeholder: Placeholder) => {
    if (editorRef.current) {
      const state = editorRef.current.state;
      const selection = state.selection;
      const placeholderText = placeholder.normalizedValue || '';
      const transactions = selection.ranges.map((value) =>
        state.update({
          changes: {
            from: value.from,
            to: value.to,
            insert: placeholderText,
          },
          selection: {
            anchor: value.from + placeholderText.length,
          },
        })
      );
      editorRef.current.update(transactions);
    }
  };

  const { refetch } = useQuery(
    ['helloWorld', language.name, translation?.text || baseTranslation],
    fetchTranslation,
    {
      enabled: false,
    }
  );

  const handleFetchAiTranslation = async () => {
    const data = await refetch();
    if (editorRef.current) {
      const state = editorRef.current.state;
      const transaction = state.update({
        changes: {
          from: 0,
          to: state.doc.length,
          insert: data.data.translatedText,
        },
      });
      editorRef.current.dispatch(transaction);
    }
  };

  return (
    <StyledContainer>
      <StyledEditor onMouseDown={(e) => e.preventDefault()}>
        {editEnabled ? (
          <TranslationEditor tools={tools} editorRef={editorRef} mode={mode} />
        ) : (
          <TranslationVisual
            text={translation?.text || ''}
            locale={language.tag}
            isPlural={keyData.keyIsPlural}
            disabled={disabled}
          />
        )}
      </StyledEditor>

      <StyledBottom>
        {Boolean(missingPlaceholders.length) && (
          <MissingPlaceholders
            placeholders={missingPlaceholders}
            onPlaceholderClick={handlePlaceholderClick}
            variant={editVal.value.parameter ? activeVariant : undefined}
            locale={language.tag}
            className="placeholders"
          />
        )}

        <StyledControls>
          <ControlsEditorSmall
            state={state}
            mode={mode}
            isBaseLanguage={language.base}
            stateChangeEnabled={canChangeState}
            onInsertBase={editEnabled ? handleInsertBase : undefined}
            onStateChange={setState}
            onModeToggle={editEnabled ? handleModeToggle : undefined}
            onFetchAiTranslation={handleFetchAiTranslation}
          />
          {editEnabled ? (
            <ControlsEditorMain
              onSave={handleSave}
              onCancel={() => handleClose(true)}
            />
          ) : (
            <ControlsEditorReadOnly onClose={() => handleClose(true)} />
          )}
        </StyledControls>
      </StyledBottom>
    </StyledContainer>
  );
};
