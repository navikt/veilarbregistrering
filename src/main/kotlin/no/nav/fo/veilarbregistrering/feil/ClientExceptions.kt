package no.nav.fo.veilarbregistrering.feil

import org.springframework.http.HttpStatus

open class RestException(val code: Int) : RuntimeException("Uventet statuskode under rest kall: $code")
class ForbiddenException(val response: String?) : RestException(HttpStatus.FORBIDDEN.value())

