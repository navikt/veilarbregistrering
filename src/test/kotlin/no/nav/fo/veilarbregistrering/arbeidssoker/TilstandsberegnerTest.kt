package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TilstandsberegnerTest {

    private val tilstandsberegner = Tilstandsberegner()

    @Test
    fun `skal ha tilstand AKTIV_ARBEIDSSOKER ved ny registrering og ikke aktiv fra før`() {
        val nyRegistrering = gyldigBrukerRegistrering()
        val eksisterendeTilstand = IkkeArbeidssoker()

        val nyTilstand = tilstandsberegner.beregnNyTilstand(nyRegistrering, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
    }

    @Test
    fun `skal beholde tilstand AKTIV_ARBEIDSSOKER ved ny registrering og allerede aktiv`() {
        val registreringsdato = LocalDateTime.now().minusMonths(2)
        val nyRegistrering = gyldigBrukerRegistrering()
        val eksisterendeTilstand = AktivArbeidssoker(fraDato = registreringsdato)

        val nyTilstand = tilstandsberegner.beregnNyTilstand(nyRegistrering, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
        assertEquals(registreringsdato, nyTilstand.fraDato)
    }

    @Test
    fun `skal starte ny periode ved ny registrering og ikke aktiv fra før`() {
        val registreringsdato = LocalDateTime.now()
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = registreringsdato)
        val eksisterendeTilstand = IkkeArbeidssoker()

        val nyTilstand = tilstandsberegner.beregnNyTilstand(nyRegistrering, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
        assertEquals(registreringsdato, nyTilstand.fraDato)
    }
}