package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;

public class OppgaveType implements Metric {

    private Type type;

    public OppgaveType(Type type) {
        this.type = type;
    }

    public static OppgaveType of(Type type) {
        return new OppgaveType(type);
    }

    @Override
    public String fieldName() {
        return "oppgavetype";
    }

    @Override
    public String value() {
        return this.type.toString();
    }

    public enum Type {
        OPPHOLDSTILLATELSE
    }

}
