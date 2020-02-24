package no.nav.fo.veilarbregistrering.arbeidsforhold;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

public interface ArbeidsforholdGateway {

    FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr);
}
