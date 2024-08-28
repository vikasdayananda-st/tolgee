package io.tolgee.hateoas.invitation

import io.tolgee.api.v2.controllers.V2InvitationController
import io.tolgee.model.Invitation
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class OrganizationInvitationModelAssembler :
  RepresentationModelAssemblerSupport<Invitation, OrganizationInvitationModel>(
    V2InvitationController::class.java,
    OrganizationInvitationModel::class.java,
  ) {
  override fun toModel(entity: Invitation): OrganizationInvitationModel {
    return OrganizationInvitationModel(
      entity.id!!,
      entity.code,
      entity.organizationRole!!.type!!,
      entity.createdAt!!,
      invitedUserName = entity.name,
      invitedUserEmail = entity.email,
    )
      .add(linkTo<V2InvitationController> { acceptInvitation(entity.code) }.withRel("accept"))
      .add(linkTo<V2InvitationController> { deleteInvitation(entity.id!!) }.withRel("delete"))
  }
}
