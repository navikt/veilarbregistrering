package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import java.time.LocalDate

interface GjelderFraDatoApi {
    fun hentGjelderFraDato(): GjelderFraDatoDto?
    fun lagreGjelderFraDato(dato: LocalDate): GjelderFraDatoDto
}
