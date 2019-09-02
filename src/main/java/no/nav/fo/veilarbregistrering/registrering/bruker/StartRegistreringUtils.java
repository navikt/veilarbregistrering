package no.nav.fo.veilarbregistrering.registrering.bruker;


import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdUtils;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.*;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType.SKAL_TIL_NY_ARBEIDSGIVER;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType.SKAL_TIL_SAMME_ARBEIDSGIVER;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;

public class StartRegistreringUtils {

    public boolean harJobbetSammenhengendeSeksAvTolvSisteManeder(
            Supplier<List<Arbeidsforhold>> arbeidsforholdSupplier,
            LocalDate dagensDato
    ) {
        return ArbeidsforholdUtils.oppfyllerBetingelseOmArbeidserfaring(arbeidsforholdSupplier.get(), dagensDato);
    }

    protected static RegistreringType beregnRegistreringType(OppfolgingStatusData oppfolgingStatusData, SykmeldtInfoData sykeforloepMetaData) {
        if (oppfolgingStatusData.isUnderOppfolging() && !ofNullable(oppfolgingStatusData.getKanReaktiveres()).orElse(false)) {
            return ALLEREDE_REGISTRERT;
        } else if (ofNullable(oppfolgingStatusData.getKanReaktiveres()).orElse(false)) {
            return REAKTIVERING;
        } else if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)
                && erSykmeldtMedArbeidsgiverOver39Uker(sykeforloepMetaData)) {
            return SYKMELDT_REGISTRERING;
        } else if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)
                && !erSykmeldtMedArbeidsgiverOver39Uker(sykeforloepMetaData)) {
            return SPERRET;
        } else {
            return ORDINAER_REGISTRERING;
        }
    }

    private static boolean erSykmeldtMedArbeidsgiverOver39Uker(SykmeldtInfoData sykeforloepMetaData) {
        return sykeforloepMetaData != null && sykeforloepMetaData.erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
    }

    public Profilering profilerBruker(
            OrdinaerBrukerRegistrering bruker,
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

    private boolean anbefalerBehovForArbeidsevnevurdering(OrdinaerBrukerRegistrering bruker) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        return HelseHinderSvar.JA.equals(besvarelse.getHelseHinder())
                || AndreForholdSvar.JA.equals(besvarelse.getAndreForhold());
    }

    private boolean anbefalerStandardInnsats(
            OrdinaerBrukerRegistrering bruker,
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

    public SykmeldtBrukerType finnSykmeldtBrukerType(SykmeldtRegistrering sykmeldtRegistrering) {
        FremtidigSituasjonSvar fremtidigSituasjon = sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon();
        if  (fremtidigSituasjon == FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER
                || fremtidigSituasjon == FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER_NY_STILLING
                || fremtidigSituasjon == FremtidigSituasjonSvar.INGEN_PASSER
        ) {
            return SKAL_TIL_SAMME_ARBEIDSGIVER;
        } else {
            return SKAL_TIL_NY_ARBEIDSGIVER;
        }

    }
}
