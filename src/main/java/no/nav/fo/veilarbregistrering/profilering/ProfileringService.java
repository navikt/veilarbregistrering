package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDate;

public class ProfileringService {

    private final ArbeidsforholdGateway arbeidsforholdGateway;

    public ProfileringService(ArbeidsforholdGateway arbeidsforholdGateway) {
        this.arbeidsforholdGateway = arbeidsforholdGateway;
    }

    public Profilering profilerBruker(
            int alder,
            Foedselsnummer fnr,
            Besvarelse besvarelse
    ) {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(fnr);

        return Profilering.of(
                besvarelse,
                alder,
                flereArbeidsforhold.harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato()));
    }

    protected LocalDate dagensDato() {
        return LocalDate.now();
    }
}
