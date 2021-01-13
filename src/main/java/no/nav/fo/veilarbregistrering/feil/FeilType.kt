package no.nav.fo.veilarbregistrering.feil

import org.springframework.http.HttpStatus.*

enum class FeilType(override val status: Int) : Feil.Type{
        INGEN_TILGANG(FORBIDDEN.value()),
        UGYLDIG_REQUEST(BAD_REQUEST.value()),
        UGYLDIG_HANDLING(CONFLICT.value()),
        FINNES_IKKE(NOT_FOUND.value()),
        UKJENT(INTERNAL_SERVER_ERROR.value());
}