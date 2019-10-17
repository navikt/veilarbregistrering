package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;

class ValideringUtilsTest {

    @Test
    void hvisMistetJobbenSaaSkalManHaSvartPaaUtdanning() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
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
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.USIKKER_JOBBSITUASJON)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalHaSvartPaaUtdanningssporsmalHvisDinSituasjonIkkeErVilFortsetteIJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
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
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering1 = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
                .setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB))
                .setSisteStilling(StillingTestdataBuilder.gyldigStilling());
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering1));

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering2 = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
                .setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB))
                .setSisteStilling(StillingTestdataBuilder.ingenYrkesbakgrunn());
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering2));
    }

    @Test
    void sporsmalSkalIkkeVaereNull() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse().setAndreForhold(null));
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void sisteStillingSkalIkkeVaereNull() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setSisteStilling(null);
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmAndreForhold() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(
                BesvarelseTestdataBuilder.gyldigBesvarelse().setAndreForhold(AndreForholdSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmHelse() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(
                BesvarelseTestdataBuilder.gyldigBesvarelse().setHelseHinder(HelseHinderSvar.INGEN_SVAR)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalHaIngenYrkesbakgrunnHvisViVetAtBrukerIkkeHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistreringUtenJobb().setSisteStilling(StillingTestdataBuilder.gyldigStilling());
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViVetAtBrukerIkkeHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistreringUtenJobb().setBesvarelse(
                BesvarelseTestdataBuilder.gyldigBesvarelseUtenJobb().setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViAlleredeVetAtBrukerHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
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
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering();
        validerBrukerRegistrering(ordinaerBrukerRegistrering);
    }

    @Test
    void valideringSkalFeileHvisBesvarelseHarNullfelt() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(
                BesvarelseTestdataBuilder.gyldigBesvarelse().setAndreForhold(null)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void valideringSkalFeileHvisStillingHarNullfelt() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setSisteStilling(
                StillingTestdataBuilder.gyldigStilling().setLabel(null)
        );
        Assertions.assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }
}