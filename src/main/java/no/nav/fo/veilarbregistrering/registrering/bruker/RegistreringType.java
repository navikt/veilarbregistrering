package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

public enum RegistreringType implements Metric {
    REAKTIVERING, SPERRET, ALLEREDE_REGISTRERT, SYKMELDT_REGISTRERING, ORDINAER_REGISTRERING;

    @Override
    public String fieldName() {
        return "registreringType";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
