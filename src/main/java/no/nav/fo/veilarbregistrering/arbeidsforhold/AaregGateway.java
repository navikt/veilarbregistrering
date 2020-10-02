package no.nav.fo.veilarbregistrering.arbeidsforhold;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

public interface AaregGateway {

    FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr);
}
