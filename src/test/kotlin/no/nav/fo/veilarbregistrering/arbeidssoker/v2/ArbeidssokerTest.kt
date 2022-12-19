package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import no.nav.fo.veilarbregistrering.arbeidssoker.IkkeArbeidssoker
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArbeidssokerTest {

    val arbeidssoker = Arbeidssoker()

    @Test
    fun `skal starte ny periode som aktiv arbeidssøker ved ny registrering og ikke aktiv fra før`() {
        val registreringsdato = LocalDateTime.now().minusMonths(2)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = registreringsdato)

        arbeidssoker.behandle(nyRegistrering)

        assertEquals(Arbeidssokerperiode(registreringsdato, null), arbeidssoker.periode())
    }

    @Test
    fun `skal overse ny registrering dersom bruker allerede er aktiv arbeidssøker, og beholde opprinnelig fradato`() {
        val gammelRegistreringsdato = LocalDateTime.now().minusMonths(2)
        val gammelRegistrering = gyldigBrukerRegistrering(opprettetDato = gammelRegistreringsdato)
        arbeidssoker.behandle(gammelRegistrering)

        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        assertEquals(Arbeidssokerperiode(gammelRegistreringsdato, null), arbeidssoker.periode())
    }

    @Test
    fun `skal overse formidlingsgruppeendring med ARBS når arbeidsøker allerede er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(nyRegistreringsdato.plusMinutes(5))
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, null), arbeidssoker.periode())
    }

    @Test
    fun `skal starte ny arbeidssøkerperiode etter formidlingsgruppeendring med ARBS når arbeidsøker ikke var aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt)
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt, null), arbeidssoker.periode())
    }

    @Test
    fun `skal avslutte gjeldende arbeidssøkerperiode etter formidlingsgruppe med ISERV når arbeidssøker er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, formidlingsgruppeEndringTidspunkt), arbeidssoker.periode())
    }

    @Test
    fun `skal overse formidlingsgruppe med ISERV når arbeidssøker ikke er aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")

        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertNull(arbeidssoker.periode())
    }
}