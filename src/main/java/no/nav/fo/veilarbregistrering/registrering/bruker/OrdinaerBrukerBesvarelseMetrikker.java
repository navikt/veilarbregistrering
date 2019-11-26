package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.SisteStillingSvar;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.metrics.MetricsFactory;

import java.util.Arrays;
import java.util.List;

class OrdinaerBrukerBesvarelseMetrikker {
    static void rapporterOrdinaerBesvarelse(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Profilering profilering) {
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
}
