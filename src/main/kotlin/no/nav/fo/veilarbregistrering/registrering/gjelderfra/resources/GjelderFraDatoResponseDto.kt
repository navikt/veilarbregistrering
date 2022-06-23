package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraDato
import java.time.LocalDate

data class GjelderFraDatoResponseDto(val dato: LocalDate?) {
    companion object {
        fun fra(gjelderFraDato: GjelderFraDato?): GjelderFraDatoResponseDto {
            return GjelderFraDatoResponseDto(gjelderFraDato?.dato)
        }
    }
}
