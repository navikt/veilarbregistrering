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

        boolean harJobbetSammenhengendeSeksAvTolvSisteManeder = flereArbeidsforhold.harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato());
        Innsatsgruppe innsatsgruppe = Innsatsgruppe.of(besvarelse, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder);

        return new Profilering(innsatsgruppe, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder);
    }

    protected LocalDate dagensDato() {
        return LocalDate.now();
    }
}
