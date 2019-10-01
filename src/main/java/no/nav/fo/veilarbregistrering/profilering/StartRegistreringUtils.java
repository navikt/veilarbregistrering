package no.nav.fo.veilarbregistrering.profilering;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdUtils;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class StartRegistreringUtils {

    //FIXME: Burde kunne være static
    public Profilering profilerBruker(
            OrdinaerBrukerRegistrering bruker,
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        return Profilering.of(bruker.getBesvarelse(), alder, harJobbetSammenhengendeSeksAvTolvSisteManeder(arbeidsforholdSupplier, dagensDato));
    }

    //FIXME: Burde kunne være static
    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato) {

        return ArbeidsforholdUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(FlereArbeidsforhold.of(arbeidsforholdSupplier.get()), dagensDato);
    }
}
