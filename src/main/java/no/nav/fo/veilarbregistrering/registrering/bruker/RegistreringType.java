package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

public enum RegistreringType implements Metric {
    REAKTIVERING, SPERRET, ALLEREDE_REGISTRERT, SYKMELDT_REGISTRERING, ORDINAER_REGISTRERING;

    protected static RegistreringType beregnRegistreringType(Oppfolgingsstatus oppfolgingsstatus, SykmeldtInfoData sykeforloepMetaData) {
        if (oppfolgingsstatus.isUnderOppfolging() && !oppfolgingsstatus.getKanReaktiveres().orElse(false)) {
            return ALLEREDE_REGISTRERT;

        } else if (oppfolgingsstatus.getKanReaktiveres().orElse(false)) {
            return REAKTIVERING;

        } else if (oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false)) {
            if (erSykmeldtMedArbeidsgiverOver39Uker(sykeforloepMetaData)) {
                return SYKMELDT_REGISTRERING;
            } else {
                return SPERRET;
            }
        } else {
            return ORDINAER_REGISTRERING;
        }
    }

    private static boolean erSykmeldtMedArbeidsgiverOver39Uker(SykmeldtInfoData sykeforloepMetaData) {
        return sykeforloepMetaData != null && sykeforloepMetaData.erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
    }

    @Override
    public String fieldName() {
        return "registreringType";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
