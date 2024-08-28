package io.tolgee.unit.formats.escaping

import io.tolgee.formats.escaping.IcuUnescper
import io.tolgee.testing.assert
import org.junit.jupiter.api.Test

class IcuUnescaperTest {
  @Test
  fun `it escapes`() {
    IcuUnescper("'{hello}', my friend!").unescaped.assert.isEqualTo("{hello}, my friend!")
  }

  @Test
  fun `it escapes apostrophes`() {
    IcuUnescper(
      "we are not entering escaped section: '''' " +
        "so it doesn't ' have to be doubled. " +
        "This sequence: '{' should be immediately closed",
    ).unescaped.assert.isEqualTo(
      "we are not entering escaped section: '' " +
        "so it doesn't ' have to be doubled. " +
        "This sequence: { should be immediately closed",
    )
  }

  @Test
  fun `it works for weird case`() {
    IcuUnescper(
      "'What ' complex '''' '{' string # ",
      false,
    ).unescaped.assert.isEqualTo("'What ' complex '' { string # ")
  }

  @Test
  fun `removes the escape char on end of string`() {
    val escaped = "Another ''''' more complex ' '''{ string }''' with many weird '} cases ''''}'"
    IcuUnescper(escaped, false)
      .unescaped.assert.isEqualTo("Another ''' more complex ' '{ string }' with many weird } cases ''}")
  }

  @Test
  fun `it it escapes escaped`() {
    IcuUnescper(
      "'''{'",
      false,
    ).unescaped.assert.isEqualTo("'{")
  }

  @Test
  fun `it escapes plurals`() {
    IcuUnescper(
      "What a '#' plural form",
      isPlural = true,
    ).unescaped.assert.isEqualTo("What a # plural form")
  }

  @Test
  fun `it unescapes inner sequence correctly`() {
    IcuUnescper(
      "'{ '' 'lakjsa'.",
      isPlural = true,
    ).unescaped.assert.isEqualTo("{ ' lakjsa'.")
  }
}
