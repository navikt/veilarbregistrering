package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;

import java.time.LocalDate;
import java.util.function.Supplier;

public class StartRegistreringUtils {

    //FIXME: Burde kunne være static
    public Profilering profilerBruker(
            int alder,
            Supplier<FlereArbeidsforhold> arbeidsforholdSupplier,
            LocalDate dagensDato, Besvarelse besvarelse
    ) {
        return Profilering.of(besvarelse, alder, arbeidsforholdSupplier.get().harJobbetSammenhengendeSeksAvTolvSisteManeder(dagensDato));
    }
}
