package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelse
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValideringUtilsTest {
    @Test
    fun `hvis mistet jobben så skal man ha svart på utdanning`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.MISTET_JOBBEN,
                sisteStilling = SisteStillingSvar.INGEN_SVAR,
                utdanning = UtdanningSvar.INGEN_SVAR,
                utdanningGodkjent = UtdanningGodkjentSvar.NEI,
                utdanningBestatt = UtdanningBestattSvar.NEI,
                helseHinder = HelseHinderSvar.NEI,
                andreForhold = AndreForholdSvar.NEI,
            )
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `hvis siste stilling spm ikke er besvart skal man vite hvorvidt bruker er i jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.USIKKER_JOBBSITUASJON,
                sisteStilling = SisteStillingSvar.INGEN_SVAR,
            )
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ha svart på utdanningsspørsmal hvis din situasjon ikke er vil fortsette i jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.MISTET_JOBBEN,
                sisteStilling = SisteStillingSvar.INGEN_SVAR,
                utdanning = UtdanningSvar.INGEN_SVAR,
                utdanningBestatt = UtdanningBestattSvar.INGEN_SVAR,
                utdanningGodkjent = UtdanningGodkjentSvar.INGEN_SVAR,
            )
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `stilling skal samsvaret med svaret pa siste stilling spm`() {
        val ordinaerBrukerRegistrering1 = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(
                sisteStilling = SisteStillingSvar.HAR_IKKE_HATT_JOBB,
            ),
            stilling = gyldigStilling()
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering1
            )
        }
        val ordinaerBrukerRegistrering2 = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(
                sisteStilling = SisteStillingSvar.HAR_HATT_JOBB,
            ),
            stilling = ingenYrkesbakgrunn
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering2
            )
        }
    }

    @Test
    fun `spørsmal skal ikke være null`() {
        val ordinaerBrukerRegistrering =
            gyldigBrukerRegistrering(
                besvarelse = gyldigBesvarelse(andreForhold = null)
            )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal svare på sporsmal om andre forhold`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(andreForhold = AndreForholdSvar.INGEN_SVAR)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal svare på spørsmal om helse`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(helseHinder = HelseHinderSvar.INGEN_SVAR)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ha ingen yrkesbakgrunn hvis vi vet at bruker ikke har hatt jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            stilling = gyldigStilling(),
            besvarelse = gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.ALDRI_HATT_JOBB,
                sisteStilling = SisteStillingSvar.INGEN_SVAR,
            )
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ikke svare på spm om siste stilling hvis vi vet at bruker ikke har hatt jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            stilling = ingenYrkesbakgrunn,
            besvarelse = gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.ALDRI_HATT_JOBB,
                sisteStilling = SisteStillingSvar.HAR_IKKE_HATT_JOBB,
            )
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ikke svare på spm om siste stilling hvis vi allerede vet at bruker har hatt jobb`() {
        var ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(
                dinSituasjon = DinSituasjonSvar.MISTET_JOBBEN,
                sisteStilling = SisteStillingSvar.INGEN_SVAR,
            )
        )
        ValideringUtils.validerBrukerRegistrering(ordinaerBrukerRegistrering)
        ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            opprettetDato = ordinaerBrukerRegistrering.opprettetDato,
            besvarelse = gyldigBesvarelse(
                sisteStilling = SisteStillingSvar.HAR_HATT_JOBB,
                dinSituasjon = DinSituasjonSvar.MISTET_JOBBEN,
            ),
            stilling = ordinaerBrukerRegistrering.sisteStilling,
            teksterForBesvarelse = ordinaerBrukerRegistrering.teksterForBesvarelse,
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `validering skal godkjenne gyldige objekter`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering()
        ValideringUtils.validerBrukerRegistrering(ordinaerBrukerRegistrering)
    }

    @Test
    fun `validering skal feile hvis besvarelse har nullfelt`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            besvarelse = gyldigBesvarelse(andreForhold = null)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `validering skal feile hvis stilling har nullfelt`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering(
            stilling = gyldigStilling(label = null)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }
}
