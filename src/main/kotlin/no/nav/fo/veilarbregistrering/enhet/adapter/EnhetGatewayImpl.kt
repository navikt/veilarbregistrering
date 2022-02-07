package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer

internal class EnhetGatewayImpl(private val enhetRestClient: EnhetRestClient) : EnhetGateway {
    override fun hentOrganisasjonsdetaljer(organisasjonsnummer: Organisasjonsnummer): Organisasjonsdetaljer? =
        enhetRestClient.hentOrganisasjon(organisasjonsnummer)?.let {
            OrganisasjonsdetaljerMapper.map(it)
        }
}