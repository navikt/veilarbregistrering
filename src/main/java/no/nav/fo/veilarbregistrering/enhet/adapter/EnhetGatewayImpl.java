package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.enhet.*;
import no.nav.fo.veilarbregistrering.enhet.Postadresse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnhetGatewayImpl implements EnhetGateway {

    private final EnhetClient enhetClient;

    public EnhetGatewayImpl(EnhetClient enhetClient) {
        this.enhetClient = enhetClient;
    }

    @Override
    public Optional<Organisasjonsdetaljer> hentOrganisasjonsdetaljer(Organisasjonsnummer organisasjonsnummer) {
        Optional<OrganisasjonDetaljer> organisasjonDetaljer = enhetClient.hentOrganisasjon(organisasjonsnummer);
        return organisasjonDetaljer.map(EnhetGatewayImpl::map);
    }

    private static Organisasjonsdetaljer map(OrganisasjonDetaljer organisasjonDetaljer) {
        return Organisasjonsdetaljer.of(
                mapForretningsadresse(organisasjonDetaljer.getForretningsadresser()),
                mapPostadresse(organisasjonDetaljer.getPostadresser()));
    }

    private static List<Forretningsadresse> mapForretningsadresse(List<ForretningsAdresse> forretningsadresser) {
        return forretningsadresser.stream().map(adresse -> map(adresse)).collect(Collectors.toList());
    }

    private static Forretningsadresse map(ForretningsAdresse adresse) {
        return new Forretningsadresse(
                Kommunenummer.of(adresse.getKommunenummer()),
                map(adresse.getGyldighetsperiode()));
    }

    private static List<Postadresse> mapPostadresse(List<no.nav.fo.veilarbregistrering.enhet.adapter.Postadresse> postadresser) {
        return postadresser.stream().map(adresse -> map(adresse)).collect(Collectors.toList());
    }

    private static Postadresse map(no.nav.fo.veilarbregistrering.enhet.adapter.Postadresse adresse) {
        return new Postadresse(
                Kommunenummer.of(adresse.getKommunenummer()),
                map(adresse.getGyldighetsperiode()));
    }

    private static Periode map(Gyldighetsperiode gyldighetsperiode) {
        return Periode.of(
                gyldighetsperiode.getFom(),
                gyldighetsperiode.getTom());
    }
}
