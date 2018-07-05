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

    public void validerBrukerRegistrering(BrukerRegistrering brukerRegistrering) {
        // TODO Skriv test for denne. FO-1020
        if (!erBesvarelseGyldig(brukerRegistrering.getBesvarelse()) || !erStillingGyldig(brukerRegistrering.getSisteStilling())) {
            throw new RuntimeException("Registreringsinformasjonen er ugyldig. Ett eller flere felt i besvarelse/stilling er null.");
        }
    }

    private boolean erStillingGyldig(Stilling stilling) {
        return stilling.getStyrk08() != null
                && stilling.getLabel() != null;
    }

    private boolean erBesvarelseGyldig(Besvarelse besvarelse) {
        return besvarelse.getDinSituasjon() != null
                && besvarelse.getSisteStilling() != null
                && besvarelse.getUtdanning() != null
                && besvarelse.getUtdanningGodkjent() != null
                && besvarelse.getUtdanningBestatt() != null
                && besvarelse.getHelseHinder() != null
                && besvarelse.getAndreForhold() != null;
    }

    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        return ArbeidsforholdUtils.oppfyllerBetingelseOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    public boolean oppfyllerBetingelseOmInaktivitet(LocalDate dagensDato, LocalDate inaktiveringsdato) {
        return Objects.isNull(inaktiveringsdato) || erDatoEldreEnnEllerLikAar(dagensDato, inaktiveringsdato, ANTALL_AAR_ISERV);
    }

    Innsatsgruppe profilerBruker(
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
        return HelseHinderSvar.JA.equals(besvarelse.getHelseHinder())
                || AndreForholdSvar.JA.equals(besvarelse.getAndreForhold());
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
                && !UtdanningSvar.INGEN_UTDANNING.equals(besvarelse.getUtdanning())
                && UtdanningBestattSvar.JA.equals(besvarelse.getUtdanningBestatt())
                && UtdanningGodkjentSvar.JA.equals(besvarelse.getUtdanningGodkjent())
                && HelseHinderSvar.NEI.equals(besvarelse.getHelseHinder())
                && AndreForholdSvar.NEI.equals(besvarelse.getAndreForhold());
    }
}
