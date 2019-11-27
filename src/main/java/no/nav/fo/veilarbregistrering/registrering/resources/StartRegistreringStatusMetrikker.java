package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

class StartRegistreringStatusMetrikker {
    static void rapporterRegistreringsstatus(StartRegistreringStatusDto registreringStatus) {
        Event event = MetricsFactory.createEvent("registrering.bruker.data");
        event.addFieldToReport("erAktivIArena", registreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT);
        event.addFieldToReport("kreverReaktivering", registreringStatus.getRegistreringType() == RegistreringType.REAKTIVERING);
        event.addFieldToReport("sykmeldUnder39uker", registreringStatus.getRegistreringType() == RegistreringType.SPERRET);
        event.addFieldToReport("sykmeldOver39uker", registreringStatus.getRegistreringType() == RegistreringType.SYKMELDT_REGISTRERING);
        event.addFieldToReport("jobbetSiste6av12Mnd", registreringStatus.getJobbetSeksAvTolvSisteManeder());
        event.report();
    }
}