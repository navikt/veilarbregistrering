package no.nav.fo.veilarbregistrering.besvarelse

import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StillingUnitTest {
    private val toLikeA = gyldigStilling("a", "l", 1 )
    private val toLikeB = gyldigStilling("a", "l", 1 )
    private val annerledes = gyldigStilling("b", "l", 1 )

    @Test
    fun `toString fungerer som forventet`() {
        assertThat(gyldigStilling().toString()).isEqualTo("Stilling(label=yrkesbeskrivelse, konseptId=1246345, styrk08=12345)")
    }

    @Test
    fun `equals fungerer som forventet`() {

        assertThat(toLikeA == toLikeB).isTrue
        assertThat(toLikeA === toLikeB).isFalse

        assertThat(toLikeA == annerledes).isFalse
    }

    @Test
    fun `hashCode fungerer som forventet`() {
        assertThat(toLikeA.hashCode()).isEqualTo(toLikeB.hashCode())
        assertThat(toLikeB.hashCode()).isNotEqualTo(annerledes.hashCode())
    }

}