package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.HasMetrics;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;

public class BrukersTilstand implements HasMetrics {

    private final SykmeldtInfoData sykmeldtInfoData;
    private final RegistreringType registreringType;
    private final Oppfolgingsstatus oppfolgingStatusData;

    public BrukersTilstand(
            Oppfolgingsstatus Oppfolgingsstatus,
            SykmeldtInfoData sykmeldtInfoData,
            boolean maksdatoToggletAv) {
        this.oppfolgingStatusData = Oppfolgingsstatus;
        this.sykmeldtInfoData = sykmeldtInfoData;
        this.registreringType = beregnRegistreringType(Oppfolgingsstatus, sykmeldtInfoData, maksdatoToggletAv);
    }

    protected static RegistreringType beregnRegistreringType(
            Oppfolgingsstatus oppfolgingsstatus,
            SykmeldtInfoData sykeforloepMetaData,
            boolean maksdatoToggletAv) {

        if (oppfolgingsstatus.isUnderOppfolging() && !oppfolgingsstatus.getKanReaktiveres().orElse(false)) {
            return ALLEREDE_REGISTRERT;

        } else if (oppfolgingsstatus.getKanReaktiveres().orElse(false)) {
            return REAKTIVERING;

        } else if (oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false)) {
            if (erSykmeldtMedArbeidsgiverOver39Uker(sykeforloepMetaData)) {
                return SYKMELDT_REGISTRERING;
            } else {
                if (maksdatoToggletAv) {
                    return SYKMELDT_REGISTRERING;
                } else
                    return SPERRET;
            }
        } else {
            return ORDINAER_REGISTRERING;
        }
    }

    private static boolean erSykmeldtMedArbeidsgiverOver39Uker(SykmeldtInfoData sykeforloepMetaData) {
        return sykeforloepMetaData != null && sykeforloepMetaData.erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
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

    public String getMaksDato() {
        return sykmeldtInfoData != null ? sykmeldtInfoData.maksDato : null;
    }

    @Override
    public List<Metric> metrics() {
        return asList(
                getFormidlingsgruppe().orElse(Formidlingsgruppe.nullable()),
                getRettighetsgruppe().orElse(Rettighetsgruppe.nullable()),
                getServicegruppe().orElse(Servicegruppe.nullable()),
                registreringType);
    }
}
