package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;

public class BrukersTilstand {

    private final RegistreringType registreringType;
    private final Oppfolgingsstatus oppfolgingStatusData;
    private final boolean harIgangsattGjenopptagbarRegistrering;

    public boolean isHarIgangsattGjenopptagbarRegistrering() {
        return harIgangsattGjenopptagbarRegistrering;
    }

    public BrukersTilstand(
            Oppfolgingsstatus Oppfolgingsstatus,
            boolean harIgangsattGjenopptagbarRegistrering) {
        this.oppfolgingStatusData = Oppfolgingsstatus;
        this.registreringType = beregnRegistreringType(Oppfolgingsstatus);
        this.harIgangsattGjenopptagbarRegistrering = registreringType == ORDINAER_REGISTRERING && harIgangsattGjenopptagbarRegistrering;
    }

    private RegistreringType beregnRegistreringType(Oppfolgingsstatus oppfolgingsstatus) {

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

    public boolean kanReaktiveres() {
        return REAKTIVERING.equals(registreringType);
    }

    public boolean ikkeErOrdinaerRegistrering() {
        return !ORDINAER_REGISTRERING.equals(registreringType);
    }

    public boolean ikkeErSykemeldtRegistrering() {
        return !SYKMELDT_REGISTRERING.equals(registreringType);
    }

    public boolean isErSykmeldtMedArbeidsgiver() {
        return oppfolgingStatusData.getErSykmeldtMedArbeidsgiver().orElse(false);
    }

    public boolean isUnderOppfolging() {
        return oppfolgingStatusData.isUnderOppfolging();
    }

    public RegistreringType getRegistreringstype() {
        return registreringType;
    }

    public Optional<Formidlingsgruppe> getFormidlingsgruppe() {
        return oppfolgingStatusData.getFormidlingsgruppe();
    }

    public Optional<Servicegruppe> getServicegruppe() {
        return oppfolgingStatusData.getServicegruppe();
    }

    public Optional<Rettighetsgruppe> getRettighetsgruppe() {
        return oppfolgingStatusData.getRettighetsgruppe();
    }

}
