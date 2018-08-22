package no.nav.fo.veilarbregistrering.service;


import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class StartRegistreringUtilsService {

    public static final String MIN_ALDER_AUTOMATISK_REGISTRERING = "MIN_ALDER_AUTOMATISK_REGISTRERING";
    public static final String MAX_ALDER_AUTOMATISK_REGISTRERING = "MAKS_ALDER_AUTOMATISK_REGISTRERING";

    public void validerBrukerRegistrering(BrukerRegistrering brukerRegistrering) {
        if (!erBesvarelseGyldig(brukerRegistrering.getBesvarelse()) || !erStillingGyldig(brukerRegistrering.getSisteStilling())) {
            throw new RuntimeException("Registreringsinformasjonen er ugyldig.");
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

    public Profilering profilerBruker(
            BrukerRegistrering bruker,
            int alder,
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        Profilering profilering = new Profilering()
                .setAlder(alder)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(harJobbetSammenhengendeSeksAvTolvSisteManeder(arbeidsforholdSupplier, dagensDato));

        if (anbefalerBehovForArbeidsevnevurdering(bruker)) {
            profilering.setInnsatsgruppe(Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING);
        } else if (anbefalerStandardInnsats(bruker, alder, profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder())) {
            profilering.setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS);
        } else {
            profilering.setInnsatsgruppe(Innsatsgruppe.SITUASJONSBESTEMT_INNSATS);
        }
        return profilering;
    }

    private boolean anbefalerBehovForArbeidsevnevurdering(BrukerRegistrering bruker) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        return HelseHinderSvar.JA.equals(besvarelse.getHelseHinder())
                || AndreForholdSvar.JA.equals(besvarelse.getAndreForhold());
    }

    private boolean anbefalerStandardInnsats(
            BrukerRegistrering bruker,
            int alder,
            boolean oppfyllerKravTilArbeidserfaring
    ) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        return (30 <= alder && alder <= 59)
                && oppfyllerKravTilArbeidserfaring
                && !svarIndikererAtBrukerIkkeOppfyllerKravTilArbeidserfaring(bruker)
                && !UtdanningSvar.INGEN_UTDANNING.equals(besvarelse.getUtdanning())
                && UtdanningBestattSvar.JA.equals(besvarelse.getUtdanningBestatt())
                && UtdanningGodkjentSvar.JA.equals(besvarelse.getUtdanningGodkjent())
                && HelseHinderSvar.NEI.equals(besvarelse.getHelseHinder())
                && AndreForholdSvar.NEI.equals(besvarelse.getAndreForhold());
    }

    private boolean svarIndikererAtBrukerIkkeOppfyllerKravTilArbeidserfaring(BrukerRegistrering bruker) {
        DinSituasjonSvar svar = bruker.getBesvarelse().getDinSituasjon();
        return svar.equals(DinSituasjonSvar.ALDRI_HATT_JOBB) || svar.equals(DinSituasjonSvar.JOBB_OVER_2_AAR);
    }
}
