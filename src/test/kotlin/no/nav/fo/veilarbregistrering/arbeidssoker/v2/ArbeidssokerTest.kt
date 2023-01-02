package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringTestdataBuilder.gyldigReaktivering
import org.junit.jupiter.api.Test
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

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(registreringsdato, null), arbeissokerVisitor.sisteArbeidsokerperiode())
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

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(gammelRegistreringsdato, null), arbeissokerVisitor.sisteArbeidsokerperiode())
    }

    @Test
    fun `skal overse formidlingsgruppeendring med ARBS når arbeidsøker allerede er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(nyRegistreringsdato.plusMinutes(5))
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, null), arbeissokerVisitor.sisteArbeidsokerperiode())
    }

    @Test
    fun `skal starte ny arbeidssøkerperiode etter formidlingsgruppeendring med ARBS når arbeidsøker ikke var aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt)
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(formidlingsgruppeEndringTidspunkt, null), arbeissokerVisitor.sisteArbeidsokerperiode())
    }

    @Test
    fun `skal avslutte gjeldende arbeidssøkerperiode etter formidlingsgruppe med ISERV når arbeidssøker er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")
        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, formidlingsgruppeEndringTidspunkt), arbeissokerVisitor.sisteArbeidsokerperiode())
    }

    @Test
    fun `skal overse formidlingsgruppe med ISERV når arbeidssøker ikke er aktiv`() {
        val formidlingsgruppeEndringTidspunkt = LocalDateTime.now()
        val formidlingsgruppeEndringEvent = formidlingsgruppeEndret(formidlingsgruppeEndringTidspunkt, "ISERV")

        arbeidssoker.behandle(formidlingsgruppeEndringEvent)

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertNull(arbeissokerVisitor.sisteArbeidsokerperiode())
    }

    @Test
    fun `skal overse reaktivering når arbeidssøker er aktiv`() {
        val nyRegistreringsdato = LocalDateTime.now().minusMonths(1)
        val nyRegistrering = gyldigBrukerRegistrering(opprettetDato = nyRegistreringsdato)
        arbeidssoker.behandle(nyRegistrering)

        val reaktiveringTidspunkt = LocalDateTime.now()
        val reaktivering = gyldigReaktivering(AktorId("1234"), reaktiveringTidspunkt)
        arbeidssoker.behandle(reaktivering)

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, null), arbeissokerVisitor.sisteArbeidsokerperiode())
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

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(reaktiveringTidspunkt, null), arbeissokerVisitor.sisteArbeidsokerperiode())
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

        var arbeissokerVisitor = ArbeidssokerVisitorDto(arbeidssoker)
        arbeidssoker.accept(arbeissokerVisitor)

        assertEquals(Arbeidssokerperiode(nyRegistreringsdato, formidlingsgruppeEndringTidspunkt), arbeissokerVisitor.sisteArbeidsokerperiode())
    }
}

data class ArbeidssokerVisitorDto(private val arbeidssoker: Arbeidssoker): ArbeidssokerVisitor {

    private var sisteArbeidssokerperiode: Arbeidssokerperiode? = null

    fun sisteArbeidsokerperiode(): Arbeidssokerperiode? = sisteArbeidssokerperiode

    init {
        arbeidssoker.accept(this)
    }

    override fun visitSistePeriode(sisteArbeidssokerperiode: Arbeidssokerperiode?) {
        this.sisteArbeidssokerperiode = sisteArbeidssokerperiode;
    }
}