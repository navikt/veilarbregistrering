package no.nav.fo.veilarbregistrering.enhet

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer.KommuneMedBydel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KommunenummerTest {

    @Test
    fun `oslo er en kommune med flere bydeler`() {
        val osloKommune = Kommunenummer.of(KommuneMedBydel.OSLO)
        assertThat(osloKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `bergen er en kommune med flere bydeler`() {
        val bergenKommune = Kommunenummer.of(KommuneMedBydel.BERGEN)
        assertThat(bergenKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `trondheim er en kommune med flere bydeler`() {
        val trondheimKommune = Kommunenummer.of(KommuneMedBydel.TRONDHEIM)
        assertThat(trondheimKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `stavanger er en kommune med flere bydeler`() {
        val stavangerKommune = Kommunenummer.of(KommuneMedBydel.STAVANGER)
        assertThat(stavangerKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `horten er ikke en kommune med flere bydeler`() {
        val hortenKommune = Kommunenummer.of("3801")
        assertThat(hortenKommune.kommuneMedBydeler()).isFalse
    }
}
