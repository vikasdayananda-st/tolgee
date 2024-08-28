package io.tolgee.hateoas.pat

import io.swagger.v3.oas.annotations.media.Schema
import io.tolgee.hateoas.userAccount.SimpleUserAccountModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "pats", itemRelation = "pat")
open class PatWithUserModel(
  @Schema(hidden = true)
  patModel: PatModel,
  val user: SimpleUserAccountModel,
) : IPatModel by patModel, RepresentationModel<PatWithUserModel>()
