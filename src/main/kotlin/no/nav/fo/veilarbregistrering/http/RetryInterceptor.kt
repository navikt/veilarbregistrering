package no.nav.fo.veilarbregistrering.http

import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException


class RetryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var throwable: Throwable? = null
        var response: Response? = null

        try {
            response = chain.proceed(chain.request())
        } catch (t: Throwable) {
            throwable = t
            var tryCount = 0
            while (throwable is SSLHandshakeException && tryCount < 3) {
                tryCount++
                try {
                    response = chain.proceed(chain.request())
                    throwable = null
                } catch (e: Exception) {
                    if (e is SSLHandshakeException) {
                        continue
                    } else {
                        throwable = e
                    }
                }
            }
        }

        return when (throwable) {
            null -> response ?: throw IllegalStateException("Error in RequestFilter, missing response")
            else -> {
                throw throwable
            }
        }
    }
}