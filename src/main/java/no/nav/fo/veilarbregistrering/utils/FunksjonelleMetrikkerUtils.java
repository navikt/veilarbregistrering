package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.SisteStillingSvar;

import java.util.Arrays;
import java.util.List;


public class FunksjonelleMetrikkerUtils {


    static boolean brukersSvarIndikererArbeidSisteManeder(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        if (brukerSvarerAtDenHarJobbetSisteMander(ordinaerBrukerRegistrering)) {
            return true;
        } else if(brukerSvarerAtDenIkkeHarJobbetSisteManeder(ordinaerBrukerRegistrering)) {
            return false;
        } else {
            return false;
        }
    }

    private static boolean brukerSvarerAtDenHarJobbetSisteMander(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
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
    private static boolean brukerSvarerAtDenIkkeHarJobbetSisteManeder(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {

        List<DinSituasjonSvar> svarSomIndikererIngenArbeidSisteManeder = Arrays.asList(
                DinSituasjonSvar.JOBB_OVER_2_AAR,
                DinSituasjonSvar.ALDRI_HATT_JOBB
        );

        return svarSomIndikererIngenArbeidSisteManeder.contains(ordinaerBrukerRegistrering.getBesvarelse().getDinSituasjon()) ||
                ordinaerBrukerRegistrering.getBesvarelse().getSisteStilling() == SisteStillingSvar.HAR_IKKE_HATT_JOBB;
    }
}
