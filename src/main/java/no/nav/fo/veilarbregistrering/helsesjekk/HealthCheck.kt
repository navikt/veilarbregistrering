package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.health.HealthCheckResult
import no.nav.common.rest.client.RestClient
import okhttp3.Request
import java.io.IOException

object HealthCheck {
    @JvmStatic
    fun performHealthCheck(baseUrl: String, useOptions: Boolean): HealthCheckResult {
        val request = Request.Builder()
            .url(baseUrl)
            .let { if (useOptions) it.method("OPTIONS", null) else it }
            .build()

        try {
            RestClient.baseClient().newCall(request)
                .execute().use {
                    return when (val status = it.code()) {
                        in 200..299 -> HealthCheckResult.healthy()
                        else -> HealthCheckResult.unhealthy("HTTP status $status")
                    }
                }
        } catch (e: IOException) {
            return HealthCheckResult.unhealthy(e)
        }
    }
}