package no.nav.fo.veilarbregistrering.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;

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

    public static void rapporterAlder(String fnr) {
        Event event = MetricsFactory.createEvent("registrering.bruker.alder");
        event.addFieldToReport("alder", utledAlderForFnr(fnr, now()));
        event.report();
    }

    public static void rapporterInvalidRegistrering(BrukerRegistrering brukerRegistrering) {
        Event event = MetricsFactory.createEvent("registrering.invalid.registrering");
        event.addFieldToReport("registrering", toJson(brukerRegistrering.getBesvarelse()));
        event.addFieldToReport("stilling", toJson(brukerRegistrering.getSisteStilling()));
        event.report();
    }

    private static String toJson(Object obj) {
        String json = "";
        try {
            json = (new ObjectMapper()).writeValueAsString(obj);
        } catch (JsonProcessingException | NullPointerException ignored) {  }
        return json;
    }

}
