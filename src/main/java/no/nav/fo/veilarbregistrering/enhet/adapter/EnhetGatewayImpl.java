package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;

import java.util.Optional;

class EnhetGatewayImpl implements EnhetGateway {

    private final EnhetRestClient enhetRestClient;

    EnhetGatewayImpl(EnhetRestClient enhetRestClient) {
        this.enhetRestClient = enhetRestClient;
    }

    @Override
    public Optional<Organisasjonsdetaljer> hentOrganisasjonsdetaljer(Organisasjonsnummer organisasjonsnummer) {
        Optional<OrganisasjonDetaljerDto> organisasjonDetaljer = enhetRestClient.hentOrganisasjon(organisasjonsnummer);
        return organisasjonDetaljer.map(OrganisasjonsdetaljerMapper::map);
    }

}
