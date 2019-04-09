package no.nav.fo.veilarbregistrering.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.metrics.Event;
import no.nav.metrics.MetricsFactory;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikkerUtils.brukerSvarerAtDenHarJobbetSisteMander;

public class FunksjonelleMetrikker {

    public static void rapporterRegistreringsstatus(StartRegistreringStatus registreringStatus) {
        Event event = MetricsFactory.createEvent("registrering.bruker.data");
        event.addFieldToReport("erAktivIArena", registreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT);
        event.addFieldToReport("kreverReaktivering", registreringStatus.getRegistreringType() == RegistreringType.REAKTIVERING);
        event.addFieldToReport("sykmeldUnder39uker", registreringStatus.getRegistreringType() == RegistreringType.SPERRET);
        event.addFieldToReport("sykmeldOver39uker", registreringStatus.getRegistreringType() == RegistreringType.SYKMELDT_REGISTRERING);
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
        event.addFieldToReport("fremtidigsituasjon", sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon() + "");
        event.report();
    }

    public static void rapporterManuellRegistrering(BrukerRegistreringType type){
        Event event = MetricsFactory.createEvent("registrering.manuell-registrering");
        event.addFieldToReport("type",  type.toString());
        event.report();
    }

    public static void rapporterManuellReaktivering(){
        Event event = MetricsFactory.createEvent("registrering.manuell-reaktivering");
        event.report();
    }

    public static void rapporterOrdinaerBesvarelse(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Profilering profilering) {
        boolean samsvarermedinfofraaareg = brukerSvarerAtDenHarJobbetSisteMander(ordinaerBrukerRegistrering)
                == profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder();

        MetricsFactory.createEvent("registrering.besvarelse.utdanning")
                .addFieldToReport("utdanning", ordinaerBrukerRegistrering.getBesvarelse().getUtdanning())
                .report();

        MetricsFactory.createEvent("registrering.besvarelse.dinsituasjon")
                .addFieldToReport("dinsituasjon", ordinaerBrukerRegistrering.getBesvarelse().getDinSituasjon())
                .report();

        MetricsFactory.createEvent("registrering.besvarelse.helseHinder")
                .addFieldToReport("helseHinder", ordinaerBrukerRegistrering.getBesvarelse().getHelseHinder())
                .report();

        MetricsFactory.createEvent("registrering.besvarelse.andreForhold")
                .addFieldToReport("andreForhold", ordinaerBrukerRegistrering.getBesvarelse().getAndreForhold())
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
