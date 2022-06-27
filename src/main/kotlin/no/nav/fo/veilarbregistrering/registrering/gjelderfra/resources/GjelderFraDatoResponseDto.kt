package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraDato

data class GjelderFraDatoResponseDto(val dato: String?) {
    companion object {
        fun fra(gjelderFraDato: GjelderFraDato?): GjelderFraDatoResponseDto {
            return if (gjelderFraDato != null) GjelderFraDatoResponseDto(gjelderFraDato.dato.toString()) else GjelderFraDatoResponseDto(null)
        }
    }
}
