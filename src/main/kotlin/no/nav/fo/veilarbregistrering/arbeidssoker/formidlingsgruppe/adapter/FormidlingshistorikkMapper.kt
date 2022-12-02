package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.bruker.Periode

internal object FormidlingshistorikkMapper {
    fun map(formidlingshistorikkDto: FormidlingshistorikkDto): Arbeidssokerperiode {
        return Arbeidssokerperiode(
            Periode(
                formidlingshistorikkDto.fraDato,
                formidlingshistorikkDto.tilDato
            )
        )
    }
}