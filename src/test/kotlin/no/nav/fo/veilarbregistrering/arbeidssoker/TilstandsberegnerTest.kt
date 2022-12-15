package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringTestdataBuilder
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TilstandsberegnerTest {

    private val tilstandsberegner = Tilstandsberegner()

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
    fun `skal starte ny periode som aktiv arbeidssøker ved ny registrering og ikke aktiv fra før`() {
        val registreringsdato = LocalDateTime.now()
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = registreringsdato)
        val eksisterendeTilstand = IkkeArbeidssoker()

        val nyTilstand = tilstandsberegner.beregnNyTilstand(nyRegistrering, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
        assertEquals(registreringsdato, nyTilstand.fraDato)
    }

    @Test
    fun `skal overse formidlingsgruppeendring med ARBS når arbeidsøker allerede er aktiv`() {
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(LocalDateTime.now())
        val registreringsdato = LocalDateTime.now().minusMinutes(5)
        val eksisterendeTilstand = AktivArbeidssoker(fraDato = registreringsdato)

        val nyTilstand = tilstandsberegner.beregnNyTilstand(formidlingsgruppeEndringEvent, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
        assertEquals(registreringsdato, nyTilstand.fraDato)
    }

    @Test
    fun `skal bli aktiv arbeidssøker etter formidlingsgruppeendring med ARBS når arbeidsøker ikke var aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt)
        val eksisterendeTilstand = IkkeArbeidssoker()

        val nyTilstand = tilstandsberegner.beregnNyTilstand(formidlingsgruppeEndringEvent, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
        assertEquals(formidlingsgruppeEndringTidspunkt, nyTilstand.fraDato)
    }

    @Test
    fun `skal starte periode for ikke arbeidssøker etter formidlingsgruppe med ISERV når arbeidssøker er aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val registreringsdato = LocalDateTime.now().minusMonths(5)
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        val eksisterendeTilstand = AktivArbeidssoker(fraDato = registreringsdato)

        val nyTilstand = tilstandsberegner.beregnNyTilstand(formidlingsgruppeEndringEvent, eksisterendeTilstand)

        assertTrue(nyTilstand is IkkeArbeidssoker)
        assertEquals(formidlingsgruppeEndringTidspunkt, nyTilstand.fraDato)
    }

    @Test
    fun `skal overse formidlingsgruppe med ISERV når ikke aktiv arbeidssøker`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val eksisterendeTilstandTidspunkt = LocalDateTime.now().minusMinutes(20)
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        val eksisterendeTilstand = IkkeArbeidssoker(fraDato = eksisterendeTilstandTidspunkt)

        val nyTilstand = tilstandsberegner.beregnNyTilstand(formidlingsgruppeEndringEvent, eksisterendeTilstand)

        assertTrue(nyTilstand is IkkeArbeidssoker)
        assertEquals(eksisterendeTilstandTidspunkt, nyTilstand.fraDato)
    }

    @Test
    fun `skal starte ny periode ved reaktivering og ikke arbeidssøker`() {
        // TODO: Bør man ha IkkeArbeidssøker *med* fraDato for å få flipp via reaktivering (for å skille fra "tom" init-tilstand)?
        //  Evt sjekk på at bruker har en tidligere AktivArbeidssøker-tilstand?
        //  Man må jo ha vært aktiv arbeidssøker tidligere. Og hva med 28-dagers grense? Skal vi ikke hensynta den her (arenaspesifikk?)?
        val eksisterendeTilstand = IkkeArbeidssoker()
        val reaktiveringTidspunkt = LocalDateTime.now()
        val reaktivering = ReaktiveringTestdataBuilder.gyldigReaktivering(AktorId("1234"), reaktiveringTidspunkt)

        val nyTilstand = tilstandsberegner.beregnNyTilstand(reaktivering, eksisterendeTilstand)

        assertTrue(nyTilstand is AktivArbeidssoker)
        assertEquals(reaktiveringTidspunkt, nyTilstand.fraDato)
    }
}