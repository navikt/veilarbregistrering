package no.nav.fo.veilarbregistrering.metrics

import no.nav.fo.veilarbregistrering.log.loggerFor
import okhttp3.Interceptor
import okhttp3.Response

class RequestTimeFilter : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.nanoTime()
        val request = chain.request()
        var throwable: Throwable? = null
        var response: Response? = null

        try {
            response = chain.proceed(request)
        } catch (t: Throwable) {
            throwable = t
        } finally {
            LOG.info(
                "Request ${request.method()} ${request.url()} completed in ${System.nanoTime() - startTime / 1e6}mS. Response code ${response?.code() ?: -1}",
                throwable
            )
        }

        return when (throwable) {
            null -> response ?: throw IllegalStateException("Error in InfluxMetrixFilter, missing response")
            else -> throw throwable
        }
    }

    companion object {
        val LOG = loggerFor<RequestTimeFilter>()
    }

}
