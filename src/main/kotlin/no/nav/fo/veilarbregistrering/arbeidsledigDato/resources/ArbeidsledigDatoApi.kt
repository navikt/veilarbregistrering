package no.nav.fo.veilarbregistrering.arbeidsledigDato.resources

import java.time.LocalDate

interface ArbeidsledigDatoApi {
    fun hentArbeidsledigDato(): ArbeidsledigDatoDto?
    fun lagreArbeidsledigDato(dato: LocalDate): ArbeidsledigDatoDto
}