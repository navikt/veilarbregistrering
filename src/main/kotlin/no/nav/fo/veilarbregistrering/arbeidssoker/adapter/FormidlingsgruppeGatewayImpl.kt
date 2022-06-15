package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppeperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode

class FormidlingsgruppeGatewayImpl(private val formidlingsgruppeRestClient: FormidlingsgruppeRestClient) :
    FormidlingsgruppeGateway {
    override fun finnArbeissokerperioder(foedselsnummer: Foedselsnummer, periode: Periode): Arbeidssokerperioder {
        val arbeidssokerperioder: List<Formidlingsgruppeperiode> =
            formidlingsgruppeRestClient.hentFormidlingshistorikk(foedselsnummer, periode)
                ?.let(FormidlingshistorikkMapper::map) ?: emptyList()
        return Arbeidssokerperioder(arbeidssokerperioder)
    }
}