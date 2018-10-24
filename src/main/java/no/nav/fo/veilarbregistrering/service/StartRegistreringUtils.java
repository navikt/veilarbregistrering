package no.nav.fo.veilarbregistrering.service;


import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import no.nav.fo.veilarbregistrering.utils.ArbeidsforholdUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.*;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.ORDINAER_REGISTRERING;

public class StartRegistreringUtils {

    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        return ArbeidsforholdUtils.oppfyllerBetingelseOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    protected static RegistreringType beregnRegistreringType(OppfolgingStatusData oppfolgingStatusData, boolean erSykmeldtMedArbeidsgiverOver39uker) {
        if (oppfolgingStatusData.isUnderOppfolging()) {
            return ALLEREDE_REGISTRERT;
        } else if (ofNullable(oppfolgingStatusData.getKanReaktiveres()).orElse(false)) {
            return REAKTIVERING;
        } else if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)
                && erSykmeldtMedArbeidsgiverOver39uker) {
            return SYKMELDT_REGISTRERING;
        } else if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)
                && !erSykmeldtMedArbeidsgiverOver39uker) {
            return SPERRET;
        } else {
            return ORDINAER_REGISTRERING;
        }
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
        return (18 <= alder && alder <= 59)
                && oppfyllerKravTilArbeidserfaring
                && !UtdanningSvar.INGEN_UTDANNING.equals(besvarelse.getUtdanning())
                && UtdanningBestattSvar.JA.equals(besvarelse.getUtdanningBestatt())
                && UtdanningGodkjentSvar.JA.equals(besvarelse.getUtdanningGodkjent())
                && HelseHinderSvar.NEI.equals(besvarelse.getHelseHinder())
                && AndreForholdSvar.NEI.equals(besvarelse.getAndreForhold());
    }
}
