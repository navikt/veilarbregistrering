package no.nav.fo.veilarbregistrering.http

import no.nav.fo.veilarbregistrering.log.logger
import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException


class RetryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        var throwable: Throwable? = null
        var tryCount = 1

        try {
            logger.info("Utfører initielt request mot ${chain.request().url()}")
            response = chain.proceed(chain.request())

        } catch (t: Throwable) {
            throwable = t

            while (throwable is SSLHandshakeException && tryCount < 4) {
                try {
                    logger.info("Retry mot ${chain.request().url()} pga SSLHandshakeException - forsøk nummer $tryCount")
                    if (response != null) {
                        logger.info("Response i try-block er ikke null - closer")
                        response.close()
                    }
                    response = chain.proceed(chain.request())
                    throwable = null
                    logger.info("Vellykket response etter forsøk nummer $tryCount")
                } catch (t: Throwable) {
                    if (response != null) {
                        logger.info("Response i catch-block er ikke null - closer")
                        response.close()
                    }
                    throwable = t
                }
                tryCount++
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