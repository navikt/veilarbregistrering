package no.nav.fo.veilarbregistrering.arbeidssoker

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

        arbeidssoker.behandle(nyRegistrering)

        assertEquals(Arbeidssokerperiode(registreringsdato, null), arbeidssoker.sistePeriode())
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
    fun `skal avslutte og starte ny periode når vi mottar formidlingsgruppeendring med ARBS og arbeidsøker allerede er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(nyRegistreringsdato.plusMinutes(5))
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(2, arbeidssoker.allePerioder().size)
        assertEquals(formidlingsgruppeEndringEvent.formidlingsgruppeEndret.toLocalDate().minusDays(1), arbeidssoker.allePerioder().get(0).tilDato?.toLocalDate())
        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringEvent.formidlingsgruppeEndret, null), arbeidssoker.sistePeriode())
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
    fun `skal håndtere negativ periode hvor formidlingsgruppe blir endret samme dag - skal gi tilDato minus 1 dag`() {
        val formidlingsgruppeEndringTidspunkt1 = LocalDate.of(2022, 8, 26).atStartOfDay()
        val formidlingsgruppeEndringEvent1 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt1, "ARBS")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent1)

        val formidlingsgruppeEndringTidspunkt2 = LocalDate.of(2022, 12, 13).atTime(2,0, 53)
        val formidlingsgruppeEndringEvent2 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt2, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent2)

        val formidlingsgruppeEndringTidspunkt3 = LocalDate.of(2022, 12, 13).atTime(15, 17,22)
        val formidlingsgruppeEndringEvent3 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt3, "ARBS")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent3)

        assertEquals(2, arbeidssoker.allePerioder().size)
        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt1, formidlingsgruppeEndringTidspunkt2.minusDays(1)), arbeidssoker.allePerioder().get(0))
        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt3, null), arbeidssoker.allePerioder().get(1))
    }
}

