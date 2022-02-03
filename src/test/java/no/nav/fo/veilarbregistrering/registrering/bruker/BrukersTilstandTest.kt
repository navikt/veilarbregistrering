package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BrukersTilstandTest {
    @Test
    fun `beregnRegistreringType gir SYKMELDT_REGISTRERING dersom bruker er sykemeldtMedArbeidsgiver`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun `Type blir ALLEREDE_REGISTRERT dersom bruker er under oppfolging`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.ALLEREDE_REGISTRERT)
    }

    @Test
    fun `Type blir ALLEREDE_REGISTRERT dersom bruker er under oppfolging og ovrige felter er null`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            null, null, null, null, null
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.ALLEREDE_REGISTRERT)
    }

    @Test
    fun `Type blir REAKTIVERING dersom bruker er under oppfolging, men kan reaktiveres`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            true,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.REAKTIVERING)
    }

    @Test
    fun `Type blir REAKTIVERING dersom bruker ikke er under oppfolging og kan reaktiveres`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            true,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.REAKTIVERING)
    }

    @Test
    fun `Type ORDINAER_REGISTRERING ved andre kombinasjoner`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            null, null, null, null, null
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus, false)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.ORDINAER_REGISTRERING)
    }
}
