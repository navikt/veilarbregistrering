package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.HasMetrics;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;

public class BrukersTilstand implements HasMetrics {

    private final SykmeldtInfoData sykmeldtInfoData;
    private final RegistreringType registreringType;
    private final Oppfolgingsstatus oppfolgingStatusData;

    BrukersTilstand(Oppfolgingsstatus oppfolgingStatusData, SykmeldtInfoData sykmeldtInfoData, RegistreringType registreringType) {
        this.oppfolgingStatusData = oppfolgingStatusData;
        this.sykmeldtInfoData = sykmeldtInfoData;
        this.registreringType = registreringType;
    }

    public boolean kanReaktiveres() {
        return REAKTIVERING.equals(registreringType);
    }

    public boolean erOrdinaerRegistrering() {
        return ORDINAER_REGISTRERING.equals(registreringType);
    }

    public boolean erRegistrertSomSykmeldtMedArbeidsgiver() {
        return SYKMELDT_REGISTRERING.equals(registreringType);
    }

    public boolean isUnderOppfolging() {
        return oppfolgingStatusData.isUnderOppfolging();
    }

    public RegistreringType getRegistreringstype() {
        return registreringType;
    }

    public Formidlingsgruppe getFormidlingsgruppe() {
        return Formidlingsgruppe.of(oppfolgingStatusData.getFormidlingsgruppe());
    }

    public Servicegruppe getServicegruppe() {
        return Servicegruppe.of(oppfolgingStatusData.getServicegruppe());
    }

    public Rettighetsgruppe getRettighetsgruppe() {
        return Rettighetsgruppe.of(oppfolgingStatusData.getRettighetsgruppe());
    }

    public String getMaksDato() {
        return sykmeldtInfoData != null ? sykmeldtInfoData.maksDato : null;
    }

    @Override
    public List<Metric> metrics() {
        return asList(getFormidlingsgruppe(), getRettighetsgruppe(), getServicegruppe(), registreringType);
    }
}
