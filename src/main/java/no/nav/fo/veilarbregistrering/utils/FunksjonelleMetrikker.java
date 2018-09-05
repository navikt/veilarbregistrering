package no.nav.fo.veilarbregistrering.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.Profilering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;

@Slf4j
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

    public static void rapporterInvalidBesvarelse(Besvarelse besvarelse) {
        String jsonBesvarelse = "";
        if (besvarelse != null) {
            try {
                jsonBesvarelse = (new ObjectMapper()).writeValueAsString(besvarelse);
            } catch (JsonProcessingException ignored) {}
        }
        Event event = MetricsFactory.createEvent("registrering.invalid.besvarelse");
        event.addFieldToReport("besvarelse", jsonBesvarelse);
        event.report();
        log.warn("Invalid besvarelse", besvarelse);
    }

    public static void rapporterInvalidStilling(Stilling stilling) {
        Event event = MetricsFactory.createEvent("registrering.invalid.stilling");
        String field = "";
        if (stilling == null) {
            field = "stilling er null";
        } else {
            if (stilling.getStyrk08() == null) {
                field += "styrk08 er null ";
            }
            if (stilling.getLabel() == null) {
                field += "label er null";
            }
        }

        event.addFieldToReport("stillingsinfo", field);
        event.report();
        log.warn("Invalid besvarelse", field);
    }

    private FunksjonelleMetrikker() {
    }
}
