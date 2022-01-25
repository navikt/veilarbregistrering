package no.nav.fo.veilarbregistrering.http

import no.nav.common.log.LogFilter
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.fo.veilarbregistrering.config.applicationNameOrNull
import no.nav.fo.veilarbregistrering.log.logger
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.slf4j.MDC
import java.math.RoundingMode
import java.text.DecimalFormat

class LogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.nanoTime()
        val incomingRequest = chain.request()
        var throwable: Throwable? = null
        var response: Response? = null
        val requestBuilder = incomingRequest.newBuilder()

        context.addCallIdHeaders(requestBuilder)
        context.addConsumerId(requestBuilder)

        val request = requestBuilder.method(incomingRequest.method(), incomingRequest.body()).build()

        try {
            response = chain.proceed(request)
            val delta = ((System.nanoTime() - startTime) / 1e6)
            logger.info("${response.code()} ${request.method()} ${request.url()} ${context.timeFormat.format(delta)}mS.")
        } catch (t: Throwable) {
            throwable = t
        }

        return when (throwable) {
            null -> response ?: throw IllegalStateException("Error in RequestFilter, missing response")
            else -> {
                throw throwable
            }
        }
    }
}

private val context = object {
    val NAV_CALL_ID_HEADER_NAMES = listOf("Nav-Call-Id", "Nav-CallId", "X-Correlation-Id")
    val timeFormat = DecimalFormat("#.##").also { it.roundingMode = RoundingMode.CEILING }

    fun addConsumerId(requestBuilder: Request.Builder) {
        applicationNameOrNull()?.let { applicationName: String ->
            requestBuilder.header(
                LogFilter.CONSUMER_ID_HEADER_NAME,
                applicationName
            )
        }
    }

    fun addCallIdHeaders(requestBuilder: Request.Builder) {
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