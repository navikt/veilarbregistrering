package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.SisteStillingSvar;
import no.nav.fo.veilarbregistrering.metrics.Event;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import no.nav.fo.veilarbregistrering.profilering.Profilering;

import java.util.Arrays;
import java.util.List;

class OrdinaerBrukerBesvarelseMetrikker {
    static void rapporterOrdinaerBesvarelse(MetricsService metricsService, OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Profilering profilering) {
        List<DinSituasjonSvar> svarSomIndikererArbeidSisteManeder = Arrays.asList(
                DinSituasjonSvar.MISTET_JOBBEN,
                DinSituasjonSvar.HAR_SAGT_OPP,
                DinSituasjonSvar.ER_PERMITTERT,
                DinSituasjonSvar.DELTIDSJOBB_VIL_MER,
                DinSituasjonSvar.VIL_BYTTE_JOBB,
                DinSituasjonSvar.VIL_FORTSETTE_I_JOBB
        );

        boolean samsvarermedinfofraaareg = (svarSomIndikererArbeidSisteManeder.contains(ordinaerBrukerRegistrering.getBesvarelse().getDinSituasjon()) ||
                ordinaerBrukerRegistrering.getBesvarelse().getSisteStilling() == SisteStillingSvar.HAR_HATT_JOBB)
                == profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder();

        metricsService.reportFields(Event.of("registrering.besvarelse.utdanning"),
                Metric.of("utdanning", ordinaerBrukerRegistrering.getBesvarelse().getUtdanning()));


        metricsService.reportFields(Event.of("registrering.besvarelse.dinsituasjon"),
                Metric.of("dinsituasjon", ordinaerBrukerRegistrering.getBesvarelse().getDinSituasjon()));


        metricsService.reportFields(Event.of("registrering.besvarelse.helseHinder"),
                Metric.of("helseHinder", ordinaerBrukerRegistrering.getBesvarelse().getHelseHinder()));


        metricsService.reportFields(Event.of("registrering.besvarelse.andreForhold"),
                Metric.of("andreForhold", ordinaerBrukerRegistrering.getBesvarelse().getAndreForhold()));


        metricsService.reportFields(Event.of("registrering.besvarelse.sistestilling.samsvarermedinfofraaareg"),
                Metric.of("samsvarermedinfofraareg", samsvarermedinfofraaareg));

    }
}
