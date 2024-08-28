package io.tolgee.model.automations

import io.tolgee.model.StandardAuditModel
import io.tolgee.model.contentDelivery.ContentDeliveryConfig
import io.tolgee.model.slackIntegration.SlackConfig
import io.tolgee.model.webhook.WebhookConfig
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

@Entity
class AutomationAction(
  @ManyToOne(fetch = FetchType.LAZY)
  var automation: Automation,
) : StandardAuditModel() {
  var type: AutomationActionType = AutomationActionType.CONTENT_DELIVERY_PUBLISH

  @ManyToOne
  var contentDeliveryConfig: ContentDeliveryConfig? = null

  @ManyToOne
  var webhookConfig: WebhookConfig? = null

  @ManyToOne
  var slackConfig: SlackConfig? = null
}
