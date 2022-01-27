package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe.Companion.of
import no.nav.fo.veilarbregistrering.bruker.Periode

internal object FormidlingshistorikkMapper {
    @JvmStatic
    fun map(response: FormidlingsgruppeResponseDto): List<Arbeidssokerperiode> =
        response.formidlingshistorikk
            ?.map(::map) ?: emptyList()

    private fun map(formidlingshistorikkDto: FormidlingshistorikkDto): Arbeidssokerperiode {
        return Arbeidssokerperiode(
            of(formidlingshistorikkDto.formidlingsgruppeKode),
            Periode(
                formidlingshistorikkDto.fraDato,
                formidlingshistorikkDto.tilDato
            )
        )
    }
}