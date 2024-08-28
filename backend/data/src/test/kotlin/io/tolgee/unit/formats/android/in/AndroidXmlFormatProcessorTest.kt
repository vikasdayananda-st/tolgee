package io.tolgee.unit.formats.android.`in`

import io.tolgee.formats.android.`in`.AndroidStringsXmlProcessor
import io.tolgee.testing.assert
import io.tolgee.unit.formats.PlaceholderConversionTestHelper
import io.tolgee.util.FileProcessorContextMockUtil
import io.tolgee.util.assertKey
import io.tolgee.util.assertLanguagesCount
import io.tolgee.util.assertSingle
import io.tolgee.util.assertSinglePlural
import io.tolgee.util.assertTranslations
import io.tolgee.util.custom
import io.tolgee.util.description
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AndroidXmlFormatProcessorTest {
  lateinit var mockUtil: FileProcessorContextMockUtil

  @BeforeEach
  fun setup() {
    mockUtil = FileProcessorContextMockUtil()
    mockUtil.mockIt("values-en/strings.xml", "src/test/resources/import/android/strings.xml")
  }

  @Test
  fun `returns correct parsed result`() {
    processFile()
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "app_name")
      .assertSingle {
        hasText("Tolgee test")
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "dogs_count")
      .assertSinglePlural {
        hasText(
          """
          {0, plural,
          one {# dog}
          other {# dogs}
          }
          """.trimIndent(),
        )
        isPluralOptimized()
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "string_array[0]")
      .assertSingle {
        hasText("First item")
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "string_array[1]")
      .assertSingle {
        hasText("Second item")
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "with_spaces")
      .assertSingle {
        hasText("Hello!")
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "with_html")
      .assertSingle {
        hasText("<b>Hello!</b>")
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "with_xliff_gs")
      .assertSingle {
        hasText(
          "<b>Hello!\n" +
            "{0, number}" +
            "</b>\n" +
            "Dont'translate this",
        )
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "with_params")
      .assertSingle {
        hasText("{0, number} {3} {2, number, .00} {3, number, scientific} %+d")
      }
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
  }

  @Test
  fun `import with placeholder conversion (disabled ICU)`() {
    mockPlaceholderConversionTestFile(convertPlaceholders = false, projectIcuPlaceholdersEnabled = false)
    processFile()
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "dogs_count")
      .assertSinglePlural {
        hasText(
          """
          {value, plural,
          one {%d dog %s '{'escape'}'}
          other {%d dogs %s}
          }
          """.trimIndent(),
        )
        isPluralOptimized()
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "string_array[0]")
      .assertSingle {
        hasText("First item %d {escape}")
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "with_params")
      .assertSingle {
        hasText("%d %4${'$'}s %.2f %e %+d {escape}")
      }
  }

  @Test
  fun `import with placeholder conversion (no conversion)`() {
    mockPlaceholderConversionTestFile(convertPlaceholders = false, projectIcuPlaceholdersEnabled = true)
    processFile()
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "dogs_count")
      .assertSinglePlural {
        hasText(
          """
          {value, plural,
          one {%d dog %s '{'escape'}'}
          other {%d dogs %s}
          }
          """.trimIndent(),
        )
        isPluralOptimized()
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "string_array[0]")
      .assertSingle {
        hasText("First item %d '{'escape'}'")
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "with_params")
      .assertSingle {
        hasText("%d %4${'$'}s %.2f %e %+d '{'escape'}'")
      }
  }

  @Test
  fun `import with placeholder conversion (with conversion)`() {
    mockPlaceholderConversionTestFile(convertPlaceholders = true, projectIcuPlaceholdersEnabled = true)
    processFile()
    mockUtil.fileProcessorContext.assertLanguagesCount(1)
    mockUtil.fileProcessorContext.assertTranslations("en", "dogs_count")
      .assertSinglePlural {
        hasText(
          """
          {0, plural,
          one {# dog {1} '{'escape'}'}
          other {# dogs {1}}
          }
          """.trimIndent(),
        )
        isPluralOptimized()
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "string_array[0]")
      .assertSingle {
        hasText("First item {0, number} '{'escape'}'")
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "string_array[1]")
      .assertSingle {
        hasText("Second item {0, number}")
      }
    mockUtil.fileProcessorContext.assertTranslations("en", "with_params")
      .assertSingle {
        hasText("{0, number} {3} {2, number, .00} {3, number, scientific} %+d '{'escape'}'")
      }
    mockUtil.fileProcessorContext.assertKey("dogs_count") {
      custom.assert.isNull()
      description.assert.isNull()
    }
  }

  @Test
  fun `placeholder conversion setting application works`() {
    PlaceholderConversionTestHelper.testFile(
      "values-en/strings.xml",
      "src/test/resources/import/android/strings_params_everywhere.xml",
      assertBeforeSettingsApplication =
        listOf(
          "{0, plural,\none {# dog {1} '{'escape'}'}\nother {# dogs {1}}\n}",
          "First item {0, number} '{'escape'}'",
          "Second item {0, number}",
          "{0, number} {3} {2, number, .00} {3, number, scientific} %+d '{'escape'}'",
        ),
      assertAfterDisablingConversion =
        listOf(
          "{value, plural,\none {%d dog %s '{'escape'}'}\nother {%d dogs %s}\n}",
          "First item %d '{'escape'}'",
          "Second item %d",
          "%d %4\$s %.2f %e %+d '{'escape'}'",
        ),
      assertAfterReEnablingConversion =
        listOf(
          "{0, plural,\none {# dog {1} '{'escape'}'}\nother {# dogs {1}}\n}",
          "First item {0, number} '{'escape'}'",
          "Second item {0, number}",
          "{0, number} {3} {2, number, .00} {3, number, scientific} %+d '{'escape'}'",
        ),
    )
  }

  private fun mockPlaceholderConversionTestFile(
    convertPlaceholders: Boolean,
    projectIcuPlaceholdersEnabled: Boolean,
  ) {
    mockUtil.mockIt(
      "values-en/strings.xml",
      "src/test/resources/import/android/strings_params_everywhere.xml",
      convertPlaceholders,
      projectIcuPlaceholdersEnabled,
    )
  }

  private fun processFile() {
    AndroidStringsXmlProcessor(mockUtil.fileProcessorContext).process()
  }
}
