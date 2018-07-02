package no.nav.fo.veilarbregistrering.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static no.nav.fo.veilarbregistrering.utils.DateUtils.erDatoEldreEnnEllerLikAar;

@Slf4j
public class StartRegistreringUtilsService {

    private static final int ANTALL_AAR_ISERV = 2;
    public static final String MIN_ALDER_AUTOMATISK_REGISTRERING = "MIN_ALDER_AUTOMATISK_REGISTRERING";
    public static final String MAX_ALDER_AUTOMATISK_REGISTRERING = "MAKS_ALDER_AUTOMATISK_REGISTRERING";

    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        return ArbeidsforholdUtils.oppfyllerBetingelseOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    public boolean oppfyllerBetingelseOmInaktivitet(LocalDate dagensDato, LocalDate inaktiveringsdato) {
        return Objects.isNull(inaktiveringsdato) || erDatoEldreEnnEllerLikAar(dagensDato, inaktiveringsdato, ANTALL_AAR_ISERV);
    }

    public Innsatsgruppe profilerBruker(
            BrukerRegistrering bruker,
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        if (anbefalerBehovForArbeidsevnevurdering(bruker)) {
            return Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING;
        }
        if (anbefalerStandardInnsats(bruker, alder, arbeidsforholdSupplier, dagensDato)) {
            return Innsatsgruppe.STANDARD_INNSATS;
        }
        return Innsatsgruppe.SITUASJONSBESTEMT_INNSATS;
    }

    private boolean anbefalerBehovForArbeidsevnevurdering(BrukerRegistrering bruker) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        return besvarelse.getHelseHinder().equals(HelseHinderSvar.JA)
                || besvarelse.getAndreForhold().equals(AndreForholdSvar.JA);
    }

    private boolean anbefalerStandardInnsats(
            BrukerRegistrering bruker,
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        return (30 <= alder && alder <= 59)
                && harJobbetSammenhengendeSeksAvTolvSisteManeder(arbeidsforholdSupplier, dagensDato)
                && !besvarelse.getUtdanning().equals(UtdanningSvar.INGEN_UTDANNING)
                && besvarelse.getUtdanningBestatt().equals(UtdanningBestattSvar.JA)
                && besvarelse.getUtdanningGodkjent().equals(UtdanningGodkjentSvar.JA)
                && besvarelse.getHelseHinder().equals(HelseHinderSvar.NEI)
                && besvarelse.getAndreForhold().equals(AndreForholdSvar.NEI);
    }
}
