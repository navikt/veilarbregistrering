package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.*;
import no.nav.fo.veilarbregistrering.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class ValideringUtilsTest {

    @Test
    void hvisMistetJobbenSaaSkalManHaSvartPaaUtdanning() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(TestUtils.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.NEI)
                .setUtdanningBestatt(UtdanningBestattSvar.NEI)
                .setHelseHinder(HelseHinderSvar.NEI)
                .setAndreForhold(AndreForholdSvar.NEI)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void hvisSisteStillingSpmIkkeErBesvartSkalManViteHvorvidtBrukerErIJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(TestUtils.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.USIKKER_JOBBSITUASJON)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalHaSvartPaaUtdanningssporsmalHvisDinSituasjonIkkeErVilFortsetteIJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(TestUtils.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningBestatt(UtdanningBestattSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void stillingSkalSamsvaretMedSvaretPaSisteStillingSpm() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering1 = TestUtils.gyldigBrukerRegistrering()
                .setBesvarelse(TestUtils.gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB))
                .setSisteStilling(TestUtils.gyldigStilling());
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering1));

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering2 = TestUtils.gyldigBrukerRegistrering()
                .setBesvarelse(TestUtils.gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB))
                .setSisteStilling(TestUtils.ingenYrkesbakgrunn());
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering2));
    }

    @Test
    void sporsmalSkalIkkeVaereNull() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(TestUtils.gyldigBesvarelse().setAndreForhold(null));
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void sisteStillingSkalIkkeVaereNull() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setSisteStilling(null);
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmAndreForhold() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(
                TestUtils.gyldigBesvarelse().setAndreForhold(AndreForholdSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmHelse() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(
                TestUtils.gyldigBesvarelse().setHelseHinder(HelseHinderSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalHaIngenYrkesbakgrunnHvisViVetAtBrukerIkkeHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistreringUtenJobb().setSisteStilling(TestUtils.gyldigStilling());
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViVetAtBrukerIkkeHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistreringUtenJobb().setBesvarelse(
                TestUtils.gyldigBesvarelseUtenJobb().setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViAlleredeVetAtBrukerHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(TestUtils.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        validerBrukerRegistrering(ordinaerBrukerRegistrering);

        ordinaerBrukerRegistrering.setBesvarelse(ordinaerBrukerRegistrering.getBesvarelse()
                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void valideringSkalGodkjenneGyldigeObjekter() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering();
        validerBrukerRegistrering(ordinaerBrukerRegistrering);
    }

    @Test
    void valideringSkalFeileHvisBesvarelseHarNullfelt() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setBesvarelse(
                TestUtils.gyldigBesvarelse().setAndreForhold(null)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void valideringSkalFeileHvisStillingHarNullfelt() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = TestUtils.gyldigBrukerRegistrering().setSisteStilling(
                TestUtils.gyldigStilling().setLabel(null)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }
}