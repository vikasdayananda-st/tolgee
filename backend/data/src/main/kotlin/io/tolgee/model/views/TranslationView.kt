package io.tolgee.model.views

import io.tolgee.constants.MtServiceType
import io.tolgee.model.enums.TranslationState

data class TranslationView(
  val id: Long,
  val text: String?,
  val state: TranslationState,
  val auto: Boolean,
  val mtProvider: MtServiceType?,
  val commentCount: Long,
  val unresolvedCommentCount: Long,
  val outdated: Boolean,
)
