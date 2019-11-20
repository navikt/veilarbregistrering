package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class OppgaveMetrikker {

    static void rapporter(GeografiskTilknytning geografiskTilknytning, String tildeltEnhetsnr) {
        Event event = MetricsFactory.createEvent("arbeid.registrering.oppgave");
        event.addTagToReport(geografiskTilknytning.fieldName(), geografiskTilknytning.value());
        event.addTagToReport("tildeltEnhetsid", "".equals(tildeltEnhetsnr) ? "INGEN_VERDI" : tildeltEnhetsnr);
        event.report();
    }
}
