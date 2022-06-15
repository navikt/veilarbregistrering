package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppeperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode

internal object FormidlingshistorikkMapper {
    fun map(response: FormidlingsgruppeResponseDto): List<Formidlingsgruppeperiode> =
        response.formidlingshistorikk?.map(::map) ?: emptyList()

    private fun map(formidlingshistorikkDto: FormidlingshistorikkDto): Formidlingsgruppeperiode {
        return Formidlingsgruppeperiode(
            Formidlingsgruppe(formidlingshistorikkDto.formidlingsgruppeKode),
            Periode(
                formidlingshistorikkDto.fraDato,
                formidlingshistorikkDto.tilDato
            )
        )
    }
}