package io.tolgee.formats.paramConvertors.out

import io.tolgee.formats.FromIcuPlaceholderConvertor
import io.tolgee.formats.MessagePatternUtil
import io.tolgee.formats.escapePercentSign

class IcuToApplePlaceholderConvertor : FromIcuPlaceholderConvertor {
  private val baseToCLikePlaceholderConvertor =
    BaseToCLikePlaceholderConvertor(
      defaultSpecifier = "@",
      numberSpecifier = "lld",
    )

  override fun convert(node: MessagePatternUtil.ArgNode): String {
    return baseToCLikePlaceholderConvertor.convert(node)
  }

  override fun convertText(string: String): String {
    return escapePercentSign(string)
  }

  override fun convertReplaceNumber(
    node: MessagePatternUtil.MessageContentsNode,
    argName: String?,
  ): String {
    return "%lld"
  }
}
