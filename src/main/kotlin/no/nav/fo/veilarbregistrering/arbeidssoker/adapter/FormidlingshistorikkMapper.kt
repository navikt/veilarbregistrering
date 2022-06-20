package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode

internal object FormidlingshistorikkMapper {
    fun map(formidlingshistorikkDto: FormidlingshistorikkDto): Arbeidssokerperiode {
        return Arbeidssokerperiode(
            Formidlingsgruppe(formidlingshistorikkDto.formidlingsgruppeKode),
            Periode(
                formidlingshistorikkDto.fraDato,
                formidlingshistorikkDto.tilDato
            )
        )
    }
}