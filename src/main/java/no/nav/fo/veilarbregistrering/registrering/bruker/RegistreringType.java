package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

import static java.util.Optional.ofNullable;

public enum RegistreringType {
    REAKTIVERING, SPERRET, ALLEREDE_REGISTRERT, SYKMELDT_REGISTRERING, ORDINAER_REGISTRERING;

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
}
