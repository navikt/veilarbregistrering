package no.nav.fo.veilarbregistrering.besvarelse

import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BesvarelseUnitTest {
    private val toLikeA = gyldigBesvarelse()
    private val toLikeB = gyldigBesvarelse()
    private val annerledes = gyldigBesvarelse().setAndreForhold(AndreForholdSvar.JA)

    @Test
    fun `toString fungerer som forventet`() {
        assertThat(gyldigBesvarelse().toString()).isEqualToIgnoringWhitespace(
            """Besvarelse(utdanning=HOYERE_UTDANNING_5_ELLER_MER, 
                utdanningBestatt=JA, 
                utdanningGodkjent=JA, 
                helseHinder=NEI, 
                andreForhold=NEI, 
                sisteStilling=HAR_HATT_JOBB, 
                dinSituasjon=JOBB_OVER_2_AAR, 
                fremtidigSituasjon=null, 
                tilbakeIArbeid=null)"""
        )
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