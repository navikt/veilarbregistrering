package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.enhet.Organisasjonsdetaljer;

public class EnhetGatewayImpl implements EnhetGateway {

    private final EnhetClient enhetClient;

    public EnhetGatewayImpl(EnhetClient enhetClient) {
        this.enhetClient = enhetClient;
    }

    @Override
    public Organisasjonsdetaljer hentOrganisasjonsdetaljer(Organisasjonsnummer organisasjonsnummer) {
        OrganisasjonDetaljer organisasjonDetaljer = enhetClient.hentOrganisasjon(organisasjonsnummer);
        return map(organisasjonDetaljer);
    }

    private static Organisasjonsdetaljer map(OrganisasjonDetaljer organisasjonDetaljer) {
        organisasjonDetaljer.getForretningsadresser();
        organisasjonDetaljer.getPostadresser();
        return null;
    }
}
