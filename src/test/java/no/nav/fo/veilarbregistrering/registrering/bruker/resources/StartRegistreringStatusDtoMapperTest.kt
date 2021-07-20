package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukersTilstand
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusDtoMapper.map
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test

class StartRegistreringStatusDtoMapperTest {

    @Test
    fun `map skal håndtere null verdier`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            null,
            null,
            null,
            null,
            null
        )
        val brukersTilstand = BrukersTilstand(oppfolgingsstatus, false)
        val (maksDato, underOppfolging, erSykmeldtMedArbeidsgiver, jobbetSeksAvTolvSisteManeder, registreringType, _, formidlingsgruppe, servicegruppe, rettighetsgruppe, geografiskTilknytning) = map(
            brukersTilstand,
            null,
            false,
            0
        )
        val softAssertions = SoftAssertions()
        softAssertions.assertThat(registreringType).isEqualTo(RegistreringType.ORDINAER_REGISTRERING)
        softAssertions.assertThat(geografiskTilknytning).isNull()
        softAssertions.assertThat(jobbetSeksAvTolvSisteManeder).isFalse
        softAssertions.assertThat(erSykmeldtMedArbeidsgiver).isFalse
        softAssertions.assertThat(underOppfolging).isFalse
        softAssertions.assertThat(formidlingsgruppe).isNull()
        softAssertions.assertThat(maksDato).isNull()
        softAssertions.assertThat(rettighetsgruppe).isNull()
        softAssertions.assertThat(servicegruppe).isNull()
        softAssertions.assertAll()
    }

    @Test
    fun `map skal håndtere verdi i alle felter`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("SERV"),
            Rettighetsgruppe.of("AAP")
        )
        val brukersTilstand = BrukersTilstand(oppfolgingsstatus, false)
        val (maksDato, underOppfolging, erSykmeldtMedArbeidsgiver, jobbetSeksAvTolvSisteManeder, registreringType, _, formidlingsgruppe, servicegruppe, rettighetsgruppe, geografiskTilknytning) = map(
            brukersTilstand,
            GeografiskTilknytning.of("030109"),
            true,
            30
        )
        val softAssertions = SoftAssertions()
        softAssertions.assertThat(registreringType).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
        softAssertions.assertThat(geografiskTilknytning).isEqualTo("030109")
        softAssertions.assertThat(jobbetSeksAvTolvSisteManeder).isTrue
        softAssertions.assertThat(erSykmeldtMedArbeidsgiver).isTrue
        softAssertions.assertThat(underOppfolging).isFalse
        softAssertions.assertThat(formidlingsgruppe).isEqualTo("IARBS")
        softAssertions.assertThat(maksDato).isNull()
        softAssertions.assertThat(rettighetsgruppe).isEqualTo("AAP")
        softAssertions.assertThat(servicegruppe).isEqualTo("SERV")
        softAssertions.assertAll()
    }

    @Test
    fun `map skal mappe erSykmeldtMedArbeidsgiver`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            true,
            false,
            Formidlingsgruppe.of("IARBS"),
            Servicegruppe.of("SERV"),
            Rettighetsgruppe.of("AAP")
        )
        val brukersTilstand = BrukersTilstand(oppfolgingsstatus, false)
        val (_, _, erSykmeldtMedArbeidsgiver) = map(
            brukersTilstand,
            GeografiskTilknytning.of("030109"),
            false,
            30
        )
        assertThat(erSykmeldtMedArbeidsgiver).isEqualTo(false)
    }
}
