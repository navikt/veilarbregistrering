package no.nav.fo.veilarbregistrering.oppfolging.adapter

import okhttp3.Headers
import javax.ws.rs.core.HttpHeaders

object Headers {

    fun buildHeaders(headers: List<Pair<String, String>>) =
        Headers.Builder().also { h ->
            headers.forEach { (k, v) ->
                if (HttpHeaders.COOKIE.equals(k, ignoreCase = true)) {
                    // Allow non-ascii characters in cookie values.
                    // For example, amp_test_cookie is set to a localized timestamp,
                    // which may include the timezone with non-ascii characters.
                    h.addUnsafeNonAscii(k, v)
                } else {
                    h.set(k, v)
                }
            }
        }.build()
}