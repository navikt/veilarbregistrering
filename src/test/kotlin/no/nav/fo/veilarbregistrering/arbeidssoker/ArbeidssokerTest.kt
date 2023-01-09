package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssoker
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Observer
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringTestdataBuilder.gyldigReaktivering
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArbeidssokerTest {

    val arbeidssoker = Arbeidssoker()

    @Test
    fun `skal starte ny periode som aktiv arbeidssøker ved ny registrering og ikke aktiv fra før`() {
        val registreringsdato = LocalDateTime.now().minusMonths(2)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = registreringsdato)

        var hendelse : String? = null

        arbeidssoker.add(object : Observer { override fun update(event: String) { hendelse = event } })
        arbeidssoker.behandle(nyRegistrering)

        assertEquals(Arbeidssokerperiode(registreringsdato, null), arbeidssoker.sistePeriode())
        assertEquals("ArbeidssokerperiodeStartetEvent", hendelse)
    }

    @Test
    fun `skal overse ny registrering dersom bruker allerede er aktiv arbeidssøker, og beholde opprinnelig fradato`() {
        val gammelRegistreringsdato = LocalDateTime.now().minusMonths(2)
        val gammelRegistrering = gyldigBrukerRegistrering(opprettetDato = gammelRegistreringsdato)
        arbeidssoker.behandle(gammelRegistrering)

        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        assertEquals(Arbeidssokerperiode(gammelRegistreringsdato, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal overse formidlingsgruppeendring med ARBS når arbeidsøker allerede er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(nyRegistreringsdato.plusMinutes(5))
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal starte ny arbeidssøkerperiode etter formidlingsgruppeendring med ARBS når arbeidsøker ikke var aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt)
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal avslutte gjeldende arbeidssøkerperiode etter formidlingsgruppe med ISERV når arbeidssøker er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, formidlingsgruppeEndringTidspunkt), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal overse formidlingsgruppe med ISERV når arbeidssøker ikke er aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")

        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertNull(arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal overse reaktivering når arbeidssøker er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val reaktiveringTidspunkt = LocalDateTime.now()
        val reaktivering = gyldigReaktivering(AktorId("1234"), reaktiveringTidspunkt)
        arbeidssoker.behandle(reaktivering)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal reaktivere arbeidssoker når en har vært inaktiv i mindre enn 28 dager`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now().minusDays(10)
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        val reaktiveringTidspunkt = LocalDateTime.now()
        val reaktivering = gyldigReaktivering(AktorId("1234"), reaktiveringTidspunkt)
        arbeidssoker.behandle(reaktivering)

        assertEquals(Arbeidssokerperiode(reaktiveringTidspunkt, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal overse reaktivering når arbeidssøker har vært inaktiv i mer enn 28 dager`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(3)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now().minusMonths(2)
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        val reaktiveringTidspunkt = LocalDateTime.now()
        val reaktivering = gyldigReaktivering(AktorId("1234"), reaktiveringTidspunkt)
        arbeidssoker.behandle(reaktivering)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, formidlingsgruppeEndringTidspunkt), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal gjenåpne tidligere periode dersom (ordinær) registrering starter samme dag som forrige ble avsluttet`() {
        val nyRegistreringsdato1 = LocalDateTime.now().minusYears(1).minusMonths(3)
        val nyRegistrering1 = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato1)
        arbeidssoker.behandle(nyRegistrering1)

        val formidlingsgruppeEndringTidspunkt1 = LocalDateTime.now().minusYears(1).minusMonths(2)
        val formidlingsgruppeEndringEvent1 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt1, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent1)

        val nyRegistreringsdato2 = LocalDateTime.now().minusMonths(3)
        val nyRegistrering2 = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato2)
        arbeidssoker.behandle(nyRegistrering2)

        val formidlingsgruppeEndringTidspunkt2 = LocalDate.now().atTime(10, 12)
        val formidlingsgruppeEndringEvent2 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt2, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent2)

        val nyRegistreringsdato3 = LocalDate.now().atTime(12, 12)
        val nyRegistrering3 = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato3)
        arbeidssoker.behandle(nyRegistrering3)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato2, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `skal gjenåpne tidligere periode dersom (formidlingsgruppe) registrering starter samme dag som forrige ble avsluttet`() {
        val nyRegistreringsdato1 = LocalDateTime.now().minusYears(1).minusMonths(3)
        val nyRegistrering1 = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato1)
        arbeidssoker.behandle(nyRegistrering1)

        val formidlingsgruppeEndringTidspunkt1 = LocalDateTime.now().minusYears(1).minusMonths(2)
        val formidlingsgruppeEndringEvent1 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt1, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent1)

        val nyRegistreringsdato2 = LocalDateTime.now().minusMonths(3)
        val nyRegistrering2 = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato2)
        arbeidssoker.behandle(nyRegistrering2)

        val formidlingsgruppeEndringTidspunkt2 = LocalDate.now().atTime(10, 12)
        val formidlingsgruppeEndringEvent2 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt2, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent2)

        val formidlingsgruppeEndringTidspunkt3 = LocalDate.now().atTime(12, 12)
        val formidlingsgruppeEndringEvent3 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt3, "ARBS")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent3)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato2, null), arbeidssoker.sistePeriode())
    }
}

