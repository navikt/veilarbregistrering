package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static org.assertj.core.api.Assertions.assertThat;

public class BrukersTilstandTest {

    @Test
    public void beregnRegistreringType_gir_SYKMELDT_REGISTRERING_når_bruker_er_sykemeldtMedArbeidsgiver() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));

        BrukersTilstand brukersTilstand = new BrukersTilstand(oppfolgingsstatus, false);
        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        assertThat(registreringType).isEqualTo(SYKMELDT_REGISTRERING);
    }
}
