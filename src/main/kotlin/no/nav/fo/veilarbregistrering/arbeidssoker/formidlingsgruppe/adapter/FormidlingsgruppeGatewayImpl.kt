package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.log.logger

class FormidlingsgruppeGatewayImpl(
    private val formidlingsgruppeRestClient: FormidlingsgruppeRestClient
) :
    FormidlingsgruppeGateway {
    override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
        val formidlingsgruppeResponseDto =
            formidlingsgruppeRestClient.hentFormidlingshistorikk(foedselsnummer, periode)
        logger.info("Fikk følgende formidlingshistorikk fra Arena sin ORDS-tjeneste: $formidlingsgruppeResponseDto")

        val arbeidssokerperioder: List<Arbeidssokerperiode> =
            formidlingsgruppeResponseDto
                ?.formidlingshistorikk
                ?.filter(::erArbeidssoker)
                ?.map(FormidlingshistorikkMapper::map)
                ?: emptyList()

        return Arbeidssokerperioder(arbeidssokerperioder)
    }

    private fun erArbeidssoker(formidlingshistorikkDto: FormidlingshistorikkDto): Boolean {
        return formidlingshistorikkDto.formidlingsgruppeKode == "ARBS"
    }
}