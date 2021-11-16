package no.nav.fo.veilarbregistrering.metrics

import no.nav.common.log.LogFilter
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.fo.veilarbregistrering.config.requireApplicationName
import no.nav.fo.veilarbregistrering.log.loggerFor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.slf4j.MDC

class LogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.nanoTime()
        val incomingRequest = chain.request()
        var throwable: Throwable? = null
        var response: Response? = null
        val requestBuilder = incomingRequest.newBuilder()

        addCallIdHeaders(requestBuilder)
        addConsumerId(requestBuilder)

        val request = requestBuilder.method(incomingRequest.method(), incomingRequest.body()).build()

        try {
            response = chain.proceed(request)
            LOG.info("${response.code()} ${request.method()} ${request.url()} ${System.nanoTime() - startTime / 1e6}mS.")
        } catch (t: Throwable) {
            throwable = t
        }

        return when (throwable) {
            null -> response ?: throw IllegalStateException("Error in RequestFilter, missing response")
            else -> {
                LOG.error("Request failed: ${request.method()} ${request.url()} $throwable")
                throw throwable
            }
        }
    }

    companion object {
        private val NAV_CALL_ID_HEADER_NAMES = listOf("Nav-Call-Id", "Nav-CallId", "X-Correlation-Id")
        val LOG = loggerFor<LogInterceptor>()

        private fun addConsumerId(requestBuilder: Request.Builder) {
            requestBuilder.header(
                LogFilter.CONSUMER_ID_HEADER_NAME,
                requireApplicationName()
            )
        }

        private fun addCallIdHeaders(requestBuilder: Request.Builder) {
            val callId = MDC.get(MDCConstants.MDC_CALL_ID)
                ?: MDC.get(MDCConstants.MDC_JOB_ID)
                ?: IdUtils.generateId() // Generate a new call-id if it is missing from the MDC context

            NAV_CALL_ID_HEADER_NAMES.forEach { headerName: String ->
                requestBuilder.header(
                    headerName,
                    callId
                )
            }
        }
    }

}
