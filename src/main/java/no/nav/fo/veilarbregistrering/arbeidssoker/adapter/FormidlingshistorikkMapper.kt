package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe.Companion.of
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeResponseDto
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingshistorikkDto
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingshistorikkMapper
import java.util.stream.Collectors
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode

internal object FormidlingshistorikkMapper {
    @JvmStatic
    fun map(response: FormidlingsgruppeResponseDto): List<Arbeidssokerperiode> =
        response.formidlingshistorikk
            .map(::map)

    private fun map(formidlingshistorikkDto: FormidlingshistorikkDto): Arbeidssokerperiode {
        return Arbeidssokerperiode(
            of(formidlingshistorikkDto.formidlingsgruppeKode),
            Periode.of(
                formidlingshistorikkDto.fraDato,
                formidlingshistorikkDto.tilDato
            )
        )
    }
}