package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.fo.veilarbregistrering.besvarelse.StillingKt.tomStilling;

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

    public static void validerBrukerRegistrering(OrdinaerBrukerRegistrering bruker) {
        Besvarelse besvarelse = bruker.getBesvarelse();
        assertFalse(besvarelseHarNull(bruker));
        assertFalse(stillingHarNull(bruker));

        assertFalse(besvarelse.getHelseHinder().equals(HelseHinderSvar.INGEN_SVAR));
        assertFalse(besvarelse.getAndreForhold().equals(AndreForholdSvar.INGEN_SVAR));
        assertFalse(bruker.getSisteStilling().equals(StillingKt.tomStilling()));

        DinSituasjonSvar dinSituasjonSvar = besvarelse.getDinSituasjon();
        UtdanningSvar utdanningSvar = besvarelse.getUtdanning();
        SisteStillingSvar sisteStillingSvar = besvarelse.getSisteStilling();

        stillingSkalSamsvareMedSisteStillingSpm(bruker);
        assertBothTrueOrBothFalse(
                situasjonerDerViVetAtBrukerenHarHattJobb.contains(dinSituasjonSvar),
                brukerHarYrkesbakgrunn(bruker) && SisteStillingSvar.INGEN_SVAR.equals(sisteStillingSvar)
        );
        assertBothTrueOrBothFalse(
                situasjonerDerViVetAtBrukerenIkkeHarHattJobb.contains(dinSituasjonSvar),
                !brukerHarYrkesbakgrunn(bruker) && SisteStillingSvar.INGEN_SVAR.equals(sisteStillingSvar)
        );

        assertBothTrueOrBothFalse(
                DinSituasjonSvar.VIL_FORTSETTE_I_JOBB.equals(dinSituasjonSvar),
                UtdanningSvar.INGEN_SVAR.equals(utdanningSvar)
        );

        assertBothTrueOrBothFalse(
                UtdanningSvar.INGEN_SVAR.equals(utdanningSvar) || UtdanningSvar.INGEN_UTDANNING.equals(utdanningSvar),
                UtdanningBestattSvar.INGEN_SVAR.equals(besvarelse.getUtdanningBestatt())
                        && UtdanningGodkjentSvar.INGEN_SVAR.equals(besvarelse.getUtdanningGodkjent())
        );

        assertBothTrueOrBothFalse(
                UtdanningSvar.INGEN_UTDANNING.equals(utdanningSvar),
                !DinSituasjonSvar.VIL_FORTSETTE_I_JOBB.equals(dinSituasjonSvar)
                        && UtdanningBestattSvar.INGEN_SVAR.equals(besvarelse.getUtdanningBestatt())
                        && UtdanningGodkjentSvar.INGEN_SVAR.equals(besvarelse.getUtdanningGodkjent())
        );
    }

    private static void stillingSkalSamsvareMedSisteStillingSpm(OrdinaerBrukerRegistrering bruker) {
        SisteStillingSvar sisteStillingSvar = bruker.getBesvarelse().getSisteStilling();
        if (SisteStillingSvar.HAR_HATT_JOBB.equals(sisteStillingSvar)) {
            assertTrue(brukerHarYrkesbakgrunn(bruker));
        } else if (SisteStillingSvar.HAR_IKKE_HATT_JOBB.equals(sisteStillingSvar)) {
            assertFalse(brukerHarYrkesbakgrunn(bruker));
        }
    }

    private static boolean stillingHarNull(OrdinaerBrukerRegistrering bruker) {
        Stilling stilling = bruker.getSisteStilling();
        return stilling == null
                || isEmpty(stilling.getStyrk08())
                || isEmpty(stilling.getLabel());
    }

    private static boolean besvarelseHarNull(OrdinaerBrukerRegistrering bruker) {
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

    private static boolean brukerHarYrkesbakgrunn(OrdinaerBrukerRegistrering bruker) {
        return !bruker.getSisteStilling().equals(StillingKt.ingenYrkesbakgrunn());
    }

    private static void assertBothTrueOrBothFalse(boolean value1, boolean value2) {
        assertTrue(value1 == value2);
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
