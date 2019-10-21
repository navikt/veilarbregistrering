package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class SykmeldtRegistreringMetrikker {
    static void rapporterSykmeldtBesvarelse(SykmeldtRegistrering sykmeldtRegistrering) {
        Event event = MetricsFactory.createEvent("registrering.sykmeldt.besvarelse");
        event.addFieldToReport("utdanning", sykmeldtRegistrering.getBesvarelse().getUtdanning() + "");
        event.addFieldToReport("fremtidigsituasjon", sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon() + "");
        event.report();
    }
}
