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

/**
 * Forsøker å benytte et format av typen "given - when - then" for testene.
 */
class ArbeidssokerTest {

    val arbeidssoker = Arbeidssoker()

    // Ulike use-case hvor vi kun fokuserer på formidlingsgruppe-events

    @Test
    fun `gitt at personen aldri har vært arbeidssøker tidligere, og vi mottar en ARBS så skal ny periode startes`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ARBS")

        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt, null), arbeidssoker.sistePeriode())
    }

    @Test
    fun `gitt at person aldri har vært arbeiddssøker tidligere, og vi mottar en ISERV så skal denne meldingen ignoreres`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")

        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertNull(arbeidssoker.sistePeriode())
    }

    @Test
    fun `gitt at formidlingsgruppe blir endret samme dag (negativ periode), så skal forrige periode avsluttes en dag før`() {
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
        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt1, atTheEndOfYesterday(formidlingsgruppeEndringTidspunkt2)), arbeidssoker.allePerioder().get(0))
        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt3, null), arbeidssoker.allePerioder().get(1))
    }

    @Test
    fun `gitt at formidlingsgruppe blir inaktivert samme dag som den ble aktivert, så skal forrige periode droppes`() {
        val formidlingsgruppeEndringTidspunkt1 = LocalDate.of(2022, 8, 26).atTime(10,19, 53)
        val formidlingsgruppeEndringEvent1 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt1, "ARBS")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent1)

        val formidlingsgruppeEndringTidspunkt2 = LocalDate.of(2022, 8, 26).atTime(14,30, 53)
        val formidlingsgruppeEndringEvent2 = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt2, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent2)

        assertEquals(0, arbeidssoker.allePerioder().size)
    }

    // Ulike use-case hvor vi også inkluderer ordinær registrering

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
    fun `skal avslutte gjeldende arbeidssøkerperiode etter formidlingsgruppe med ISERV når arbeidssøker er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, atTheEndOfYesterday(formidlingsgruppeEndringTidspunkt)), arbeidssoker.sistePeriode())
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

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, atTheEndOfYesterday(formidlingsgruppeEndringTidspunkt)), arbeidssoker.sistePeriode())
    }

    private fun atTheEndOfYesterday(localDateTime: LocalDateTime): LocalDateTime {
        return localDateTime.toLocalDate().atTime(23, 59, 59).minusDays(1)
    }

}

