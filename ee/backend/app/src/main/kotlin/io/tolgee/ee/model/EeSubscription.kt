package io.tolgee.ee.model

import io.hypersistence.utils.hibernate.type.array.EnumArrayType
import io.tolgee.api.EeSubscriptionDto
import io.tolgee.api.IEeSubscription
import io.tolgee.api.SubscriptionStatus
import io.tolgee.constants.Feature
import io.tolgee.model.AuditModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.Type
import java.util.*

@Entity
@Table(schema = "ee")
class EeSubscription : AuditModel(), IEeSubscription {
  @field:Id
  override val id: Int = 1

  @field:NotBlank
  lateinit var licenseKey: String

  @field:ColumnDefault("Plan")
  lateinit var name: String

  override var currentPeriodEnd: Date? = null

  override var cancelAtPeriodEnd: Boolean = false

  @Type(EnumArrayType::class, parameters = [Parameter(name = EnumArrayType.SQL_ARRAY_TYPE, value = "varchar")])
  @Column(name = "enabled_features", columnDefinition = "varchar[]")
  override var enabledFeatures: Array<Feature> = arrayOf()
    get() {
      return if (status != SubscriptionStatus.ERROR && status != SubscriptionStatus.CANCELED) field else arrayOf()
    }

  @Enumerated(EnumType.STRING)
  @ColumnDefault("ACTIVE")
  override var status: SubscriptionStatus = SubscriptionStatus.ACTIVE

  override var lastValidCheck: Date? = null

  fun toDto(): EeSubscriptionDto {
    return EeSubscriptionDto(
      licenseKey = licenseKey,
      name = name,
      currentPeriodEnd = currentPeriodEnd,
      cancelAtPeriodEnd = cancelAtPeriodEnd,
      enabledFeatures = enabledFeatures,
      status = status,
      lastValidCheck = lastValidCheck,
    )
  }
}
