package io.tolgee.ee.api.v2.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.tolgee.ee.api.v2.hateoas.assemblers.EeSubscriptionModelAssembler
import io.tolgee.ee.data.SetLicenseKeyDto
import io.tolgee.ee.service.EeSubscriptionServiceImpl
import io.tolgee.hateoas.ee.PrepareSetEeLicenceKeyModel
import io.tolgee.hateoas.ee.eeSubscription.EeSubscriptionModel
import io.tolgee.security.authentication.RequiresSuperAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v2/ee-license/")
@Tag(name = "EE Licence (only for self-hosted instances)")
class EeLicenseController(
  private val eeSubscriptionService: EeSubscriptionServiceImpl,
  private val eeSubscriptionModelAssembler: EeSubscriptionModelAssembler,
) {
  @PutMapping("set-license-key")
  @Operation(summary = "Sets the EE licence key for this instance")
  @RequiresSuperAuthentication
  fun setLicenseKey(
    @RequestBody body: SetLicenseKeyDto,
  ): EeSubscriptionModel {
    val eeSubscription = eeSubscriptionService.setLicenceKey(body.licenseKey)
    return eeSubscriptionModelAssembler.toModel(eeSubscription.toDto())
  }

  @PostMapping("prepare-set-license-key")
  @Operation(summary = "Returns info about the upcoming EE subscription")
  @RequiresSuperAuthentication
  fun prepareSetLicenseKey(
    @RequestBody body: SetLicenseKeyDto,
  ): PrepareSetEeLicenceKeyModel {
    return eeSubscriptionService.prepareSetLicenceKey(body.licenseKey)
  }

  @PutMapping("/refresh")
  @RequiresSuperAuthentication
  fun refreshSubscription(): EeSubscriptionModel? {
    eeSubscriptionService.refreshSubscription()
    val eeSubscription = eeSubscriptionService.findSubscriptionEntity() ?: return null
    return eeSubscriptionModelAssembler.toModel(eeSubscription.toDto())
  }

  @GetMapping("info")
  @Operation(summary = "Returns the info about the current EE subscription")
  @RequiresSuperAuthentication
  fun getInfo(): EeSubscriptionModel? {
    val eeSubscription = eeSubscriptionService.findSubscriptionEntity()
    return eeSubscription?.let { eeSubscriptionModelAssembler.toModel(it.toDto()) }
  }

  @PutMapping("release-license-key")
  @Operation(summary = "Removes the EE licence key from this instance")
  @RequiresSuperAuthentication
  fun release() {
    eeSubscriptionService.releaseSubscription()
  }
}
