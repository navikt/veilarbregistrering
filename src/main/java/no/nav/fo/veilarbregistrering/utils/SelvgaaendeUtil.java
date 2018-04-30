package no.nav.fo.veilarbregistrering.utils;


import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;

public class SelvgaaendeUtil {

    public static final String NUS_KODE_0 = "0";
    public static final String NUS_KODE_2 = "2";
    public static final boolean HAR_HELSEUTFORDRINGER = true;

    public static boolean erSelvgaaende(BrukerRegistrering bruker, StartRegistreringStatus startRegistreringStatus) {
        return erBesvarelseneValidertSomSelvgaaende(bruker) &&
                !startRegistreringStatus.isUnderOppfolging() &&
                startRegistreringStatus.isOppfyllerKravForAutomatiskRegistrering();
    }

    public static boolean erBesvarelseneValidertSomSelvgaaende(BrukerRegistrering bruker) {
        return !(bruker.getNusKode().equals(NUS_KODE_0)
                || bruker.isHarHelseutfordringer() == HAR_HELSEUTFORDRINGER);
    }
}

