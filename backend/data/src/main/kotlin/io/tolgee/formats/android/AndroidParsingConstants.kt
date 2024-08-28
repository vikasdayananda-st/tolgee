package io.tolgee.formats.android

object AndroidParsingConstants {
  val supportedTags =
    setOf(
      "b", "i", "cite", "dfn", "em",
      "big", "small", "font",
      "tt", "s", "strike", "del", "u",
      "sup", "sub", "ul", "li",
      "br", "div", "p", "a",
    )

  val spacesWithoutNewLines = setOf(' ', '\t', '\u0020', '\u2008', '\u2003')

  val spaces = spacesWithoutNewLines + '\n'
}
