package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.SisteStillingSvar;

import java.util.Arrays;
import java.util.List;


public class FunksjonelleMetrikkerUtils {

    static boolean brukerSvarerAtDenHarJobbetSisteMander(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        List<DinSituasjonSvar> svarSomIndikererArbeidSisteManeder = Arrays.asList(
                DinSituasjonSvar.MISTET_JOBBEN,
                DinSituasjonSvar.HAR_SAGT_OPP,
                DinSituasjonSvar.ER_PERMITTERT,
                DinSituasjonSvar.DELTIDSJOBB_VIL_MER,
                DinSituasjonSvar.VIL_BYTTE_JOBB,
                DinSituasjonSvar.VIL_FORTSETTE_I_JOBB
        );

        return svarSomIndikererArbeidSisteManeder.contains(ordinaerBrukerRegistrering.getBesvarelse().getDinSituasjon()) ||
                ordinaerBrukerRegistrering.getBesvarelse().getSisteStilling() == SisteStillingSvar.HAR_HATT_JOBB;
    }

}
