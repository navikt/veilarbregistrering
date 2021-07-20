package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BrukersTilstandTest {
    @Test
    fun `beregnRegistreringType gir SYKMELDT_REGISTRERING når bruker er sykemeldtMedArbeidsgiver`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe.of("IYT")
        )
        val brukersTilstand = BrukersTilstand(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }
}
