package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode

internal object FormidlingshistorikkMapper {
    fun hentArbeidssokerperioderOgMap(response: FormidlingsgruppeResponseDto): List<Arbeidssokerperiode> =
        response.formidlingshistorikk?.filter{ erArbeidssoker(it) }?.map(::tilArbeidssokerperiode) ?: emptyList()

    private fun tilArbeidssokerperiode(formidlingshistorikkDto: FormidlingshistorikkDto): Arbeidssokerperiode {
        return Arbeidssokerperiode(
            Formidlingsgruppe(formidlingshistorikkDto.formidlingsgruppeKode),
            Periode(
                formidlingshistorikkDto.fraDato,
                formidlingshistorikkDto.tilDato
            )
        )
    }

    private fun erArbeidssoker(formidlingshistorikkDto: FormidlingshistorikkDto): Boolean {
        return formidlingshistorikkDto.formidlingsgruppeKode == "ARBS"
    }
}