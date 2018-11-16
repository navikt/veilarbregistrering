package no.nav.fo.veilarbregistrering.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikkerUtils.brukerSvarerAtDenHarJobbetSisteMander;

@Slf4j
public class FunksjonelleMetrikker {

    public static void rapporterRegistreringsstatus(StartRegistreringStatus registreringStatus) {
        Event event = MetricsFactory.createEvent("registrering.bruker.data");
        boolean sykmeldtOver39Uker = registreringStatus.getRegistreringType() == RegistreringType.SYKMELDT_REGISTRERING;
        boolean sykmeldtUnder39Uker = registreringStatus.getRegistreringType() == RegistreringType.SPERRET;
        boolean reaktivering = registreringStatus.getRegistreringType() == RegistreringType.REAKTIVERING;
        event.addFieldToReport("erAktivIArena", registreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT);
        event.addFieldToReport("kreverReaktivering", reaktivering);
        event.addFieldToReport("sykmeldUnder39uker", sykmeldtUnder39Uker);
        event.addFieldToReport("sykmeldOver39uker", sykmeldtOver39Uker);
        log.info("Rapportering av registreringsstatus : reaktivering {}, sykmeldtUnder39Uker {}, sykmeldtOver39Uker {}", reaktivering, sykmeldtUnder39Uker, sykmeldtOver39Uker);
        event.addFieldToReport("jobbetSiste6av12Mnd", registreringStatus.getJobbetSeksAvTolvSisteManeder());
        event.report();
    }

    public static void rapporterProfilering(Profilering profilering) {
        Event event = MetricsFactory.createEvent("registrering.bruker.profilering");
        event.addFieldToReport("innsatsgruppe", profilering.getInnsatsgruppe());
        event.report();
    }

    public static void rapporterSykmeldtBesvarelse(SykmeldtRegistrering sykmeldtRegistrering) {
        Event event = MetricsFactory.createEvent("registrering.sykmeldt.besvarelse");
        event.addFieldToReport("utdanning", sykmeldtRegistrering.getBesvarelse().getUtdanning() + "");
        event.report();
    }

    public static void rapporterOrdinaerBesvarelse(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Profilering profilering) {
        boolean samsvarermedinfofraaareg = brukerSvarerAtDenHarJobbetSisteMander(ordinaerBrukerRegistrering)
                == profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder();

        MetricsFactory.createEvent("registrering.besvarelse.utdanning")
                .addFieldToReport("utdanning", ordinaerBrukerRegistrering.getBesvarelse().getUtdanning())
                .report();

        MetricsFactory.createEvent("registrering.besvarelse.helseHinder")
                .addFieldToReport("helseHinder", ordinaerBrukerRegistrering.getBesvarelse().getHelseHinder())
                .report();

        MetricsFactory.createEvent("registrering.besvarelse.sistestilling.samsvarermedinfofraaareg")
                .addFieldToReport("samsvarermedinfofraareg", samsvarermedinfofraaareg)
                .report();
    }

    public static void rapporterAlder(String fnr) {
        Event event = MetricsFactory.createEvent("registrering.bruker.alder");
        event.addFieldToReport("alder", utledAlderForFnr(fnr, now()));
        event.report();
    }

    public static void rapporterInvalidRegistrering(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        Event event = MetricsFactory.createEvent("registrering.invalid.registrering");
        event.addFieldToReport("registrering", toJson(ordinaerBrukerRegistrering.getBesvarelse()));
        event.addFieldToReport("stilling", toJson(ordinaerBrukerRegistrering.getSisteStilling()));
        event.report();
    }

    private static String toJson(Object obj) {
        String json = "";
        try {
            json = (new ObjectMapper()).writeValueAsString(obj);
        } catch (JsonProcessingException ignored) {  }
        return json;
    }

}
