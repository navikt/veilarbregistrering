package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdUtils;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class StartRegistreringUtils {

    //FIXME: Burde kunne være static
    public Profilering profilerBruker(
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato, Besvarelse besvarelse
    ) {
        return Profilering.of(besvarelse, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder(arbeidsforholdSupplier, dagensDato));
    }

    //FIXME: Burde kunne være static
    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato) {

        return ArbeidsforholdUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(FlereArbeidsforhold.of(arbeidsforholdSupplier.get()), dagensDato);
    }
}
