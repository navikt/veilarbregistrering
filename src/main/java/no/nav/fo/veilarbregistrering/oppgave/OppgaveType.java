package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.registrering.bruker.Status;

public enum OppgaveType implements Metric {
    OPPHOLDSTILLATELSE, UTVANDRET;

    @Override
    public String fieldName() {
        return "oppgavetype";
    }

    @Override
    public String value() {
        return this.toString();
    }

    public static OppgaveType of(Status status) {
        if (status == Status.DOD_UTVANDRET_ELLER_FORSVUNNET) {
            return OppgaveType.UTVANDRET;
        } else if (status == Status.MANGLER_ARBEIDSTILLATELSE) {
            return OppgaveType.OPPHOLDSTILLATELSE;
        } else {
            throw new OpprettOppgaveException(String.format("Klarte ikke å mappe aktiveringsstatusen %s til OppgaveType", status));
        }
    }
}
