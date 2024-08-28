import { components } from 'tg.service/apiSchema.generated';

type LanguageModel = components['schemas']['LanguageModel'];

export function getPreselectedLanguages(
  allLanguages: LanguageModel[],
  preselected: string[]
) {
  return (
    allLanguages
      .map((l) => l.tag)
      .filter((tag) => preselected?.includes(tag)) ?? []
  );
}
