package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum OppgaveType implements Metric {
    OPPHOLDSTILLATELSE, DOD_UTVANDRET;

    @Override
    public String fieldName() {
        return "oppgavetype";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
