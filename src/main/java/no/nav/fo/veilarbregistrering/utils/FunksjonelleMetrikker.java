package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

public class FunksjonelleMetrikker {

    public static void rapporterRegistreringsstatus(AktivStatus aktivStatus, StartRegistreringStatus registreringStatus) {
        Event event = MetricsFactory.createEvent("registrering.bruker.data");
        event.addFieldToReport("underOppfolging", aktivStatus.isUnderOppfolging());
        event.addFieldToReport("erAktivIArena", aktivStatus.isAktiv());
        event.addFieldToReport("kreverReaktivering", registreringStatus.isKreverReaktivering());
        event.addFieldToReport("jobbetSiste6av12Mnd", registreringStatus.isJobbetSeksAvTolvSisteManeder());
        event.report();
    }

    private FunksjonelleMetrikker() {
    }
}
