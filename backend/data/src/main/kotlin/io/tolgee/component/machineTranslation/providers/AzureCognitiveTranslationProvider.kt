package io.tolgee.component.machineTranslation.providers

import io.tolgee.component.machineTranslation.MtValueProvider
import io.tolgee.configuration.tolgee.machineTranslation.AzureCognitiveTranslationProperties
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class AzureCognitiveTranslationProvider(
  private val azureCognitiveTranslationProperties: AzureCognitiveTranslationProperties,
  private val azureCognitiveApiService: AzureCognitiveApiService,
) : AbstractMtValueProvider() {
  override val isEnabled: Boolean
    get() = !azureCognitiveTranslationProperties.authKey.isNullOrEmpty()

  override fun translateViaProvider(params: ProviderTranslateParams): MtValueProvider.MtResult {
    val result =
      azureCognitiveApiService.translate(
        params.text,
        params.sourceLanguageTag.uppercase(),
        params.targetLanguageTag.uppercase(),
      )
    return MtValueProvider.MtResult(
      result,
      params.text.length * 100,
    )
  }

  override val supportedLanguages =
    arrayOf(
      "af", "am", "ar", "as", "az", "ba", "bg", "bn", "bo", "bs", "ca", "cs", "cy", "da", "de", "dv", "el", "en", "es",
      "et", "eu", "fa", "fi", "fil", "fj", "fo", "fr", "fr-CA", "ga", "gl", "gu", "he", "hi", "hr", "hsb", "ht", "hu",
      "hy", "id", "ikt", "is", "it", "iu", "iu-Latn", "ja", "ka", "kk", "km", "kmr", "kn", "ko", "ku", "ky", "lo", "lt",
      "lv", "lzh", "mg", "mi", "mk", "ml", "mn-Cyrl", "mn-Mong", "mr", "ms", "mt", "mww", "my", "nb", "ne", "nl", "or",
      "otq", "pa", "pl", "prs", "ps", "pt", "pt-PT", "ro", "ru", "sk", "sl", "sm", "so", "sq", "sr-Cyrl", "sr-Latn",
      "sv",
      "sw", "ta", "te", "th", "ti", "tk", "tlh-Latn", "tlh-Piqd", "to", "tr", "tt", "ty", "ug", "uk", "ur", "uz", "vi",
      "yua", "yue", "zh-Hans", "zh-Hant", "zu",
    )
  override val formalitySupportingLanguages: Array<String>? = null
}
