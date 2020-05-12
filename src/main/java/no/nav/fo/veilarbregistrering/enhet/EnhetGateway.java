package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;

public interface EnhetGateway {

    Organisasjonsdetaljer hentOrganisasjonsdetaljer(Organisasjonsnummer organisasjonsnummer);
}
