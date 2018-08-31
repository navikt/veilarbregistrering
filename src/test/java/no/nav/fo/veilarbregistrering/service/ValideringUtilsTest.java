package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.SisteStillingSvar;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class ValideringUtilsTest {

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