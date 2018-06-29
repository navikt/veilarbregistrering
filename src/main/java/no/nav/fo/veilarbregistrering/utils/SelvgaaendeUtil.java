package no.nav.fo.veilarbregistrering.utils;


import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.domain.besvarelse.HelseHinderSvar;

public class SelvgaaendeUtil {

    public static final String NUS_KODE_0 = "0";

    public static boolean erSelvgaaende(BrukerRegistrering bruker, StartRegistreringStatus startRegistreringStatus) {
        return erBesvarelseneValidertSomSelvgaaende(bruker) &&
                !startRegistreringStatus.isUnderOppfolging();
    }

    public static boolean erBesvarelseneValidertSomSelvgaaende(BrukerRegistrering bruker) {
        return !(bruker.getNusKode().equals(NUS_KODE_0)
                || bruker.getBesvarelse().getHelseHinder() == HelseHinderSvar.JA);
    }
}