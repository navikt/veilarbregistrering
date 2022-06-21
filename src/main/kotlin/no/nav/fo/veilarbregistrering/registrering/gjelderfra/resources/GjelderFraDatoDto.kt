package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraDato
import java.time.LocalDate

data class GjelderFraDatoDto(val dato: LocalDate?) {
    companion object {
        fun fra(gjelderFraDato: GjelderFraDato?): GjelderFraDatoDto {
            return GjelderFraDatoDto(gjelderFraDato?.dato)
        }
    }
}
