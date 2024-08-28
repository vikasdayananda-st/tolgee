package io.tolgee.model.automations

import io.tolgee.model.Project
import io.tolgee.model.StandardAuditModel
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Automation(
  @ManyToOne(fetch = FetchType.LAZY)
  var project: Project,
) : StandardAuditModel() {
  @OneToMany(mappedBy = "automation", orphanRemoval = true)
  var triggers: MutableList<AutomationTrigger> = mutableListOf()

  @OneToMany(mappedBy = "automation", orphanRemoval = true)
  var actions: MutableList<AutomationAction> = mutableListOf()
}
