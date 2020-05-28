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
            int alder, //TODO: Fjerne alder fra signatur - utledes av fnr på innsiden, eller hentes fra PDL. Det første krever litt innsats med testdata.
            Foedselsnummer foedselsnummer,
            LocalDate dagensDato,
            Besvarelse besvarelse) {

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(foedselsnummer);

        return Profilering.of(
                besvarelse,
                alder,
                flereArbeidsforhold.harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato));
    }
}
