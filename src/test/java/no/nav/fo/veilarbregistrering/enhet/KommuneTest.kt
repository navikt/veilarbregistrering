package no.nav.fo.veilarbregistrering.enhet

import no.nav.fo.veilarbregistrering.enhet.Kommune.KommuneMedBydel.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KommuneTest {

    @Test
    fun `oslo er en kommune med flere bydeler`() {
        val osloKommune = Kommune.medBydel(OSLO)
        assertThat(osloKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `bergen er en kommune med flere bydeler`() {
        val bergenKommune = Kommune.medBydel(BERGEN)
        assertThat(bergenKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `trondheim er en kommune med flere bydeler`() {
        val trondheimKommune = Kommune.medBydel(TRONDHEIM)
        assertThat(trondheimKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `stavanger er en kommune med flere bydeler`() {
        val stavangerKommune = Kommune.medBydel(STAVANGER)
        assertThat(stavangerKommune.kommuneMedBydeler()).isTrue
    }

    @Test
    fun `horten er ikke en kommune med flere bydeler`() {
        val hortenKommune = Kommune("3801")
        assertThat(hortenKommune.kommuneMedBydeler()).isFalse
    }
}
