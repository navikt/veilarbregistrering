package no.nav.fo.veilarbregistrering.http

import no.nav.fo.veilarbregistrering.log.logger
import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException


class RetryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        logger.info("Utfører request mot ${chain.request().url()}")
        return utforRequestMedRetry(chain, 0)
    }

    private fun utforRequestMedRetry(chain: Interceptor.Chain, tryCount: Int): Response {
        var response: Response? = null
        var throwable: Throwable? = null

        try {
            response = chain.proceed(chain.request())

        } catch (t: Throwable) {
            throwable = t

            if (throwable is SSLHandshakeException && tryCount < 4) {
                logger.info("Utfører retry mot ${chain.request().url()} pga SSLHandshakeException - forsøk nummer $tryCount")
                if (response != null) {
                    logger.info("Response i catch-block er ikke null - closer")
                    response.close()
                }
                utforRequestMedRetry(chain, tryCount + 1)
            }
        }

        return when (throwable) {
            null -> response ?: throw IllegalStateException("Error in RetryInterceptor, missing response")
            else -> {
                throw throwable
            }
        }
    }
}