package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class ValideringUtilsTest {

    @Test
    void hvisMistetJobbenSaaSkalManHaSvartPaaUtdanning() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.NEI)
                .setUtdanningBestatt(UtdanningBestattSvar.NEI)
                .setHelseHinder(HelseHinderSvar.NEI)
                .setAndreForhold(AndreForholdSvar.NEI)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void hvisSisteStillingSpmIkkeErBesvartSkalManViteHvorvidtBrukerErIJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.USIKKER_JOBBSITUASJON)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalHaSvartPaaUtdanningssporsmalHvisDinSituasjonIkkeErVilFortsetteIJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningBestatt(UtdanningBestattSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void stillingSkalSamsvaretMedSvaretPaSisteStillingSpm() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering1 = gyldigBrukerRegistrering()
                .setBesvarelse(gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB))
                .setSisteStilling(gyldigStilling());
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering1));

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering2 = gyldigBrukerRegistrering()
                .setBesvarelse(gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB))
                .setSisteStilling(ingenYrkesbakgrunn());
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering2));
    }

    @Test
    void sporsmalSkalIkkeVaereNull() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse().setAndreForhold(null));
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void sisteStillingSkalIkkeVaereNull() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setSisteStilling(null);
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmAndreForhold() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
                gyldigBesvarelse().setAndreForhold(AndreForholdSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmHelse() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
                gyldigBesvarelse().setHelseHinder(HelseHinderSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalHaIngenYrkesbakgrunnHvisViVetAtBrukerIkkeHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistreringUtenJobb().setSisteStilling(gyldigStilling());
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViVetAtBrukerIkkeHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistreringUtenJobb().setBesvarelse(
                gyldigBesvarelseUtenJobb().setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViAlleredeVetAtBrukerHarHattJobb() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        validerBrukerRegistrering(ordinaerBrukerRegistrering);

        ordinaerBrukerRegistrering.setBesvarelse(ordinaerBrukerRegistrering.getBesvarelse()
                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void valideringSkalGodkjenneGyldigeObjekter() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering();
        validerBrukerRegistrering(ordinaerBrukerRegistrering);
    }

    @Test
    void valideringSkalFeileHvisBesvarelseHarNullfelt() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
                gyldigBesvarelse().setAndreForhold(null)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }

    @Test
    void valideringSkalFeileHvisStillingHarNullfelt() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setSisteStilling(
                gyldigStilling().setLabel(null)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(ordinaerBrukerRegistrering));
    }
}