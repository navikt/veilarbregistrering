package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;

public class BrukersTilstandUtenSperret extends BrukersTilstand {

    public BrukersTilstandUtenSperret(Oppfolgingsstatus Oppfolgingsstatus, SykmeldtInfoData sykmeldtInfoData) {
        super(Oppfolgingsstatus, sykmeldtInfoData);
    }

    @Override
    protected RegistreringType beregnRegistreringType(
            Oppfolgingsstatus oppfolgingsstatus,
            SykmeldtInfoData sykeforloepMetaData) {

        if (oppfolgingsstatus.isUnderOppfolging() && !oppfolgingsstatus.getKanReaktiveres().orElse(false)) {
            return ALLEREDE_REGISTRERT;

        } else if (oppfolgingsstatus.getKanReaktiveres().orElse(false)) {
            return REAKTIVERING;

        } else if (oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false)) {
            return SYKMELDT_REGISTRERING;

        } else {
            return ORDINAER_REGISTRERING;
        }
    }

}
