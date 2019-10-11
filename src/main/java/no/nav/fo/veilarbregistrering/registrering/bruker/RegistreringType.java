package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

import static java.util.Optional.ofNullable;

public enum RegistreringType {
    REAKTIVERING, SPERRET, ALLEREDE_REGISTRERT, SYKMELDT_REGISTRERING, ORDINAER_REGISTRERING;

    protected static RegistreringType beregnRegistreringType(Oppfolgingsstatus oppfolgingsstatus, SykmeldtInfoData sykeforloepMetaData) {
        if (oppfolgingsstatus.isUnderOppfolging() && !ofNullable(oppfolgingsstatus.getKanReaktiveres()).orElse(false)) {
            return ALLEREDE_REGISTRERT;
        } else if (ofNullable(oppfolgingsstatus.getKanReaktiveres()).orElse(false)) {
            return REAKTIVERING;
        } else if (ofNullable(oppfolgingsstatus.getErSykmeldtMedArbeidsgiver()).orElse(false)
                && erSykmeldtMedArbeidsgiverOver39Uker(sykeforloepMetaData)) {
            return SYKMELDT_REGISTRERING;
        } else if (ofNullable(oppfolgingsstatus.getErSykmeldtMedArbeidsgiver()).orElse(false)
                && !erSykmeldtMedArbeidsgiverOver39Uker(sykeforloepMetaData)) {
            return SPERRET;
        } else {
            return ORDINAER_REGISTRERING;
        }
    }

    private static boolean erSykmeldtMedArbeidsgiverOver39Uker(SykmeldtInfoData sykeforloepMetaData) {
        return sykeforloepMetaData != null && sykeforloepMetaData.erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
    }
}
