package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public enum OppgaveType implements Metric {
    OPPHOLDSTILLATELSE;

    @Override
    public String fieldName() {
        return "oppgavetype";
    }

    @Override
    public String value() {
        return this.toString();
    }
}
