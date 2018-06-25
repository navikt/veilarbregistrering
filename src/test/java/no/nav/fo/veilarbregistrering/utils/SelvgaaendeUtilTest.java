package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.service.Konstanter.*;
import static no.nav.fo.veilarbregistrering.utils.SelvgaaendeUtil.erBesvarelseneValidertSomSelvgaaende;
import static no.nav.fo.veilarbregistrering.utils.SelvgaaendeUtil.erSelvgaaende;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SelvgaaendeUtilTest {

    @Test
    void skalValidereSelvgaaendeUnderoppfolging() {
        BrukerRegistrering bruker = getBrukerBesvarelse();
        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(true);
        assertThat(erSelvgaaende(bruker, startRegistreringStatus )).isFalse();
    }

    @Test
    void skalValidereSelvgaaendeOppfyllerkrav() {
        BrukerRegistrering bruker = getBrukerBesvarelse();
        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(false);
        assertThat(erSelvgaaende(bruker, startRegistreringStatus )).isFalse();
    }

    @Test
    void brukerMedBesvarelseNus_Kode_0_SkalFeile() {
        BrukerRegistrering bruker = new BrukerRegistrering()
                .setNusKode(NUS_KODE_0)
                .setYrkesPraksis(null)
                .setOpprettetDato(null)
                .setEnigIOppsummering(ENIG_I_OPPSUMMERING)
                .setOppsummering(OPPSUMMERING)
                .setHarHelseutfordringer(HAR_INGEN_HELSEUTFORDRINGER);
        assertThat(erBesvarelseneValidertSomSelvgaaende(bruker)).isFalse();
    }

    @Test
    void brukerMedBesvarelseNus_Kode_2_SkalIkkeFeile() {
        BrukerRegistrering bruker = new BrukerRegistrering()
                .setNusKode(NUS_KODE_2)
                .setYrkesPraksis(null)
                .setOpprettetDato(null)
                .setEnigIOppsummering(ENIG_I_OPPSUMMERING)
                .setOppsummering(OPPSUMMERING);
        assertThat(erBesvarelseneValidertSomSelvgaaende(bruker)).isTrue();
    }

    private BrukerRegistrering getBrukerBesvarelse() {
        return  new BrukerRegistrering()
                .setNusKode(NUS_KODE_4)
                .setYrkesPraksis(null)
                .setOpprettetDato(null)
                .setEnigIOppsummering(ENIG_I_OPPSUMMERING)
                .setOppsummering(OPPSUMMERING)
                .setHarHelseutfordringer(HAR_INGEN_HELSEUTFORDRINGER);
    }

}