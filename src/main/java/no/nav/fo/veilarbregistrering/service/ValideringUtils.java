package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValideringUtils {

    private static final List<DinSituasjonSvar> situasjonerDerViVetAtBrukerenHarHattJobb = Arrays.asList(
            DinSituasjonSvar.MISTET_JOBBEN,
            DinSituasjonSvar.HAR_SAGT_OPP,
            DinSituasjonSvar.ER_PERMITTERT,
            DinSituasjonSvar.DELTIDSJOBB_VIL_MER,
            DinSituasjonSvar.VIL_BYTTE_JOBB,
            DinSituasjonSvar.VIL_FORTSETTE_I_JOBB
    );
    private static final List<DinSituasjonSvar> situasjonerDerViVetAtBrukerenIkkeHarHattJobb = Collections.singletonList(
            DinSituasjonSvar.ALDRI_HATT_JOBB
    );
    private static final Stilling ingenYrkesbakgrunn = new Stilling("X", -1L, "X");
    private static final Stilling tomStilling = new Stilling("", -1L, "-1");

    public static void validerBrukerRegistrering(BrukerRegistrering bruker) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        assertFalse(besvarelseHarNull(bruker));
        assertFalse(stillingHarNull(bruker));

        assertFalse(besvarelse.getHelseHinder().equals(HelseHinderSvar.INGEN_SVAR));
        assertFalse(besvarelse.getAndreForhold().equals(AndreForholdSvar.INGEN_SVAR));
        assertFalse(bruker.getSisteStilling().equals(tomStilling));

        DinSituasjonSvar dinSituasjonSvar = besvarelse.getDinSituasjon();
        UtdanningSvar utdanningSvar = besvarelse.getUtdanning();

        if (situasjonerDerViVetAtBrukerenHarHattJobb.contains(dinSituasjonSvar)) {
            brukerSkalIkkeHaBesvartSisteStillingSpm(bruker);
            assertFalse(brukerHarIngenYrkesbakgrunn(bruker));
        }

        if (situasjonerDerViVetAtBrukerenIkkeHarHattJobb.contains(dinSituasjonSvar)) {
            brukerSkalIkkeHaBesvartSisteStillingSpm(bruker);
            assertTrue(brukerHarIngenYrkesbakgrunn(bruker));
        }

        if (dinSituasjonSvar.equals(DinSituasjonSvar.VIL_FORTSETTE_I_JOBB)) {
            assertTrue(utdanningSvar.equals(UtdanningSvar.INGEN_SVAR));
            assertTrue(besvarelse.getUtdanningBestatt().equals(UtdanningBestattSvar.INGEN_SVAR));
            assertTrue(besvarelse.getUtdanningGodkjent().equals(UtdanningGodkjentSvar.INGEN_SVAR));
        }

        if (utdanningSvar.equals(UtdanningSvar.INGEN_UTDANNING)) {
            assertTrue(besvarelse.getUtdanningBestatt().equals(UtdanningBestattSvar.INGEN_SVAR));
            assertTrue(besvarelse.getUtdanningGodkjent().equals(UtdanningGodkjentSvar.INGEN_SVAR));
        }
    }

    private static boolean stillingHarNull(BrukerRegistrering bruker) {
        Stilling stilling = bruker.getSisteStilling();
        return stilling == null
                || isEmpty(stilling.getStyrk08())
                || isEmpty(stilling.getLabel());
    }

    private static boolean besvarelseHarNull(BrukerRegistrering bruker) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        return besvarelse == null
                || besvarelse.getDinSituasjon() == null
                || besvarelse.getSisteStilling() == null
                || besvarelse.getUtdanning() == null
                || besvarelse.getUtdanningGodkjent() == null
                || besvarelse.getUtdanningBestatt() == null
                || besvarelse.getHelseHinder() == null
                || besvarelse.getAndreForhold() == null;
    }

    private static boolean brukerHarIngenYrkesbakgrunn(BrukerRegistrering bruker) {
        return bruker.getSisteStilling().equals(ValideringUtils.ingenYrkesbakgrunn);
    }

    private static void brukerSkalIkkeHaBesvartSisteStillingSpm(BrukerRegistrering bruker) {
        assertTrue(bruker.getBesvarelse().getSisteStilling().equals(SisteStillingSvar.INGEN_SVAR));
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new RuntimeException("Registreringsinformasjonen er ugyldig.");
        }
    }

    private static void assertFalse(boolean value) {
        assertTrue(!value);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
