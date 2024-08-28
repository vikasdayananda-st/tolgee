/*
 * Copyright (c) 2020. Tolgee
 */

package io.tolgee.dtos.response

import java.util.*

data class ScreenshotDTO(
  val id: Long,
  val filename: String,
  val createdAt: Date,
)
