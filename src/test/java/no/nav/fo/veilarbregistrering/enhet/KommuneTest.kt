package no.nav.fo.veilarbregistrering.enhet

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KommuneTest {

    @Test
    fun `oslo er en kommune med flere bydeler`() {
        val osloKommune = Kommune.of(KommuneMedBydel.OSLO)
        assertThat(osloKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `bergen er en kommune med flere bydeler`() {
        val bergenKommune = Kommune.of(KommuneMedBydel.BERGEN)
        assertThat(bergenKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `trondheim er en kommune med flere bydeler`() {
        val trondheimKommune = Kommune.of(KommuneMedBydel.TRONDHEIM)
        assertThat(trondheimKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `stavanger er en kommune med flere bydeler`() {
        val stavangerKommune = Kommune.of(KommuneMedBydel.STAVANGER)
        assertThat(stavangerKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `horten er ikke en kommune med flere bydeler`() {
        val hortenKommune = Kommune("3801")
        assertThat(hortenKommune.kommuneMedBydeler()).isFalse
    }
}
