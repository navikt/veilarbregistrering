package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class BrukersTilstandTest {
    @Test
    fun `beregnRegistreringType gir SYKMELDT_REGISTRERING dersom bruker er sykemeldtMedArbeidsgiver`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            false,
            true,
            Formidlingsgruppe("IARBS"),
            Servicegruppe("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING)
    }

    @Test
    fun `Type blir ALLEREDE_REGISTRERT dersom bruker er under oppfolging`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            false,
            true,
            Formidlingsgruppe("IARBS"),
            Servicegruppe("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.ALLEREDE_REGISTRERT)
    }

    @Test
    fun `Type blir ALLEREDE_REGISTRERT dersom bruker er under oppfolging og ovrige felter er null`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            null, null, null, null, null
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.ALLEREDE_REGISTRERT)
    }

    @Test
    fun `Type blir REAKTIVERING dersom bruker er under oppfolging, men kan reaktiveres`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            true,
            true,
            true,
            Formidlingsgruppe("IARBS"),
            Servicegruppe("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.REAKTIVERING)
    }

    @Test
    fun `Type blir REAKTIVERING dersom bruker ikke er under oppfolging og kan reaktiveres`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            true,
            true,
            Formidlingsgruppe("IARBS"),
            Servicegruppe("VURDI"),
            Rettighetsgruppe("IYT")
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.REAKTIVERING)
    }

    @Test
    fun `Type ORDINAER_REGISTRERING ved andre kombinasjoner`() {
        val oppfolgingsstatus = Oppfolgingsstatus(
            false,
            null, null, null, null, null
        )
        val brukersTilstand = BrukersTilstand.create(oppfolgingsstatus)
        val registreringType = brukersTilstand.registreringstype
        assertThat(registreringType).isEqualTo(RegistreringType.ORDINAER_REGISTRERING)
    }

    @Test
    fun `Kjente ulikheter i oppfolgingsstatus får samme utfall i registreringstype`() {
        val ukjentBrukerGammelKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=null, formidlingsgruppe=null, servicegruppe=null, rettighetsgruppe=null)
        val ukjentBrukerNyKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=false, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ISERV"), servicegruppe=Servicegruppe("IVURD"), rettighetsgruppe=Rettighetsgruppe("IYT"))

        assertThat(beregnRegistreringType(ukjentBrukerGammelKilde)).isEqualTo(beregnRegistreringType(ukjentBrukerNyKilde))

        val kanReaktiveresNullGammelKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ISERV"), servicegruppe=Servicegruppe("IVURD"), rettighetsgruppe=Rettighetsgruppe("IYT"))
        val kanReaktiveresFalseNyKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=false, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ISERV"), servicegruppe=Servicegruppe("IVURD"), rettighetsgruppe=Rettighetsgruppe("IYT"))

        assertThat(beregnRegistreringType(kanReaktiveresNullGammelKilde)).isEqualTo(beregnRegistreringType(kanReaktiveresFalseNyKilde))

        val ARBSGammelKilde = Oppfolgingsstatus(isUnderOppfolging=true, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ARBS"), servicegruppe=Servicegruppe("BFORM"), rettighetsgruppe=Rettighetsgruppe("IYT"))
        val ISERVNyKilde = Oppfolgingsstatus(isUnderOppfolging=true, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ISERV"), servicegruppe=Servicegruppe("BFORM"), rettighetsgruppe=Rettighetsgruppe("IYT"))

        assertThat(beregnRegistreringType(ARBSGammelKilde)).isEqualTo(beregnRegistreringType(ISERVNyKilde))

        val underOppfolgingArenaKoderGammelKilde = Oppfolgingsstatus(isUnderOppfolging=true, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=true, formidlingsgruppe= Formidlingsgruppe(kode="IARBS"), servicegruppe=Servicegruppe("VURDI"), rettighetsgruppe=Rettighetsgruppe("IYT"))
        val underOppfolgingNullNyKilde = Oppfolgingsstatus(isUnderOppfolging=true, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=null, formidlingsgruppe=null, servicegruppe=null, rettighetsgruppe=null)

        assertThat(beregnRegistreringType(underOppfolgingArenaKoderGammelKilde)).isEqualTo(beregnRegistreringType(underOppfolgingNullNyKilde))

    }

    @Disabled
    @Test
    fun `Ulikheter som trigger avvik i registreringstype`() {
        val kanReaktiveresNullGammelKilde2 = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ISERV"), servicegruppe=Servicegruppe("BFORM"), rettighetsgruppe=Rettighetsgruppe("IYT"))
        val kanReaktiveresTrueNyKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=true, erSykmeldtMedArbeidsgiver=false, formidlingsgruppe= Formidlingsgruppe("ISERV"), servicegruppe=Servicegruppe("BFORM"), rettighetsgruppe=Rettighetsgruppe("IYT"))

        assertThat(beregnRegistreringType(kanReaktiveresNullGammelKilde2)).isEqualTo(beregnRegistreringType(kanReaktiveresTrueNyKilde))

        val ukjentBrukerGammelKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=null, formidlingsgruppe=null, servicegruppe=null, rettighetsgruppe=null)
        val erSykmeldtMedArbeidsgiverTrueNyKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=true, formidlingsgruppe= Formidlingsgruppe("IARBS"), servicegruppe= Servicegruppe("VURDI"), rettighetsgruppe=Rettighetsgruppe("IYT"))

        assertThat(beregnRegistreringType(ukjentBrukerGammelKilde)).isEqualTo(beregnRegistreringType(erSykmeldtMedArbeidsgiverTrueNyKilde))

        val ikkeUnderOppfolgingArenaKoderGammelKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=true, formidlingsgruppe= Formidlingsgruppe(kode="IARBS"), servicegruppe=Servicegruppe("VURDI"), rettighetsgruppe=Rettighetsgruppe("IYT"))
        val ikkeUnderOppfolgingNullNyKilde = Oppfolgingsstatus(isUnderOppfolging=false, kanReaktiveres=null, erSykmeldtMedArbeidsgiver=null, formidlingsgruppe=null, servicegruppe=null, rettighetsgruppe=null)

        assertThat(beregnRegistreringType(ikkeUnderOppfolgingArenaKoderGammelKilde)).isEqualTo(beregnRegistreringType(ikkeUnderOppfolgingNullNyKilde))
    }


}
