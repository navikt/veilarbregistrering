package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

public class FunksjonelleMetrikker {

    public static void rapporterRegistreringsstatus(StartRegistreringStatus registreringStatus) {
        Event event = MetricsFactory.createEvent("registrering.bruker.data");
        event.addFieldToReport("erAktivIArena", registreringStatus.isUnderOppfolging());
        event.addFieldToReport("kreverReaktivering", registreringStatus.getKreverReaktivering());
        event.addFieldToReport("jobbetSiste6av12Mnd", registreringStatus.getJobbetSeksAvTolvSisteManeder());
        event.report();
    }

    public static void rapporterProfilering(Profilering profilering) {
        Event event = MetricsFactory.createEvent("registrering.bruker.profilering");
        event.addFieldToReport("innsatsgruppe", profilering.getInnsatsgruppe());
        event.report();
    }

    private FunksjonelleMetrikker() {
    }
}
