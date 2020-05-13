package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;

import java.util.Optional;

public interface EnhetGateway {

    Optional<Organisasjonsdetaljer> hentOrganisasjonsdetaljer(Organisasjonsnummer organisasjonsnummer);
}
