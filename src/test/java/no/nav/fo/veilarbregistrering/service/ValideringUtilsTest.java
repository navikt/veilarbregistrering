package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.*;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class ValideringUtilsTest {

    @Test
    void hvisSisteStillingSpmIkkeErBesvartSkalManViteHvorvidtBrukerErIJobb() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.USIKKER_JOBBSITUASJON)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void skalHaSvartPaaUtdanningssporsmalHvisDinSituasjonIkkeErVilFortsetteIJobb() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningBestatt(UtdanningBestattSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void stillingSkalSamsvaretMedSvaretPaSisteStillingSpm() {
        BrukerRegistrering brukerRegistrering1 = gyldigBrukerRegistrering()
                .setBesvarelse(gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB))
                .setSisteStilling(gyldigStilling());
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering1));

        BrukerRegistrering brukerRegistrering2 = gyldigBrukerRegistrering()
                .setBesvarelse(gyldigBesvarelse()
                        .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB))
                .setSisteStilling(ingenYrkesbakgrunn());
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering2));
    }

    @Test
    void sporsmalSkalIkkeVaereNull() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse().setAndreForhold(null));
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void sisteStillingSkalIkkeVaereNull() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setSisteStilling(null);
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmAndreForhold() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
                gyldigBesvarelse().setAndreForhold(AndreForholdSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void skalSvarePaaSporsmalOmHelse() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
                gyldigBesvarelse().setHelseHinder(HelseHinderSvar.INGEN_SVAR)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void skalHaIngenYrkesbakgrunnHvisViVetAtBrukerIkkeHarHattJobb() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistreringUtenJobb().setSisteStilling(gyldigStilling());
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViVetAtBrukerIkkeHarHattJobb() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistreringUtenJobb().setBesvarelse(
                gyldigBesvarelseUtenJobb().setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void skalIkkeSvarePaaSpmOmSisteStillingHvisViAlleredeVetAtBrukerHarHattJobb() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        );
        validerBrukerRegistrering(brukerRegistrering);

        brukerRegistrering.setBesvarelse(brukerRegistrering.getBesvarelse()
                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void valideringSkalGodkjenneGyldigeObjekter() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering();
        validerBrukerRegistrering(brukerRegistrering);
    }

    @Test
    void valideringSkalFeileHvisBesvarelseHarNullfelt() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
                gyldigBesvarelse().setAndreForhold(null)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }

    @Test
    void valideringSkalFeileHvisStillingHarNullfelt() {
        BrukerRegistrering brukerRegistrering = gyldigBrukerRegistrering().setSisteStilling(
                gyldigStilling().setLabel(null)
        );
        assertThrows(RuntimeException.class, () -> validerBrukerRegistrering(brukerRegistrering));
    }
}