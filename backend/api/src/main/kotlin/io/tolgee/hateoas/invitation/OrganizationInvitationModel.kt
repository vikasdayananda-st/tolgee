package io.tolgee.hateoas.invitation

import io.tolgee.model.enums.OrganizationRoleType
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.util.*

@Relation(collectionRelation = "organizationInvitations", itemRelation = "organizationInvitation")
open class OrganizationInvitationModel(
  val id: Long,
  val code: String,
  val type: OrganizationRoleType,
  val createdAt: Date,
  val invitedUserName: String?,
  val invitedUserEmail: String?,
) : RepresentationModel<OrganizationInvitationModel>()
