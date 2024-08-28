package io.tolgee.component

import io.sentry.protocol.User
import io.sentry.spring.jakarta.SentryUserProvider
import io.tolgee.security.authentication.AuthenticationFacade
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class TolgeeSentryUserProvider(
  private val authenticationFacade: AuthenticationFacade,
) : SentryUserProvider {
  override fun provideUser(): User? {
    return authenticationFacade.authenticatedUserOrNull?.let { user ->
      return User().apply {
        name = user.username
        username = user.username
        email = user.username
        id = user.id.toString()
        ipAddress = getClientIpAddressIfServletRequestExist()
      }
    }
  }

  fun getClientIpAddressIfServletRequestExist(): String? {
    if (RequestContextHolder.getRequestAttributes() == null) {
      return null
    }

    val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
    for (header in IP_HEADER_CANDIDATES) {
      val ipList = request.getHeader(header)
      if (ipList != null && ipList.isNotEmpty() && !"unknown".equals(ipList, ignoreCase = true)) {
        val ip = ipList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.firstOrNull()
        return ip
      }
    }

    return request.remoteAddr
  }

  companion object {
    private val IP_HEADER_CANDIDATES =
      arrayOf(
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR",
      )
  }
}
