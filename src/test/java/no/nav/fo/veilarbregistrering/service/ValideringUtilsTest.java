package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigBesvarelse;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigStilling;
import static org.junit.jupiter.api.Assertions.*;

class ValideringUtilsTest {

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