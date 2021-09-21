package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelse
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelseUtenJobb
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.ingenYrkesbakgrunn
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistreringUtenJobb
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValideringUtilsTest {
    @Test
    fun `hvis mistet jobben så skal man ha svart på utdanning`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.NEI)
                .setUtdanningBestatt(UtdanningBestattSvar.NEI)
                .setHelseHinder(HelseHinderSvar.NEI)
                .setAndreForhold(AndreForholdSvar.NEI)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `hvis siste stilling spm ikke er besvart skal man vite hvorvidt bruker er i jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.USIKKER_JOBBSITUASJON)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ha svart på utdanningsspørsmal hvis din situasjon ikke er vil fortsette i jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
                .setUtdanning(UtdanningSvar.INGEN_SVAR)
                .setUtdanningBestatt(UtdanningBestattSvar.INGEN_SVAR)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.INGEN_SVAR)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `stilling skal samsvaret med svaret pa siste stilling spm`() {
        val ordinaerBrukerRegistrering1 = gyldigBrukerRegistrering()
            .setBesvarelse(
                gyldigBesvarelse()
                    .setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB)
            )
            .setSisteStilling(gyldigStilling())
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering1
            )
        }
        val ordinaerBrukerRegistrering2 = gyldigBrukerRegistrering()
            .setBesvarelse(
                gyldigBesvarelse()
                    .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
            )
            .setSisteStilling(ingenYrkesbakgrunn())
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering2
            )
        }
    }

    @Test
    fun `spørsmal skal ikke være null`() {
        val ordinaerBrukerRegistrering =
            gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse().setAndreForhold(null))
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `siste stilling skal ikke være null`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setSisteStilling(null)
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal svare på sporsmal om andre forhold`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse().setAndreForhold(AndreForholdSvar.INGEN_SVAR)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal svare på spørsmal om helse`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse().setHelseHinder(HelseHinderSvar.INGEN_SVAR)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ha ingen yrkesbakgrunn hvis vi vet at bruker ikke har hatt jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistreringUtenJobb().setSisteStilling(gyldigStilling())
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ikke svare på spm om siste stilling hvis vi vet at bruker ikke har hatt jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistreringUtenJobb().setBesvarelse(
            gyldigBesvarelseUtenJobb().setSisteStilling(SisteStillingSvar.HAR_IKKE_HATT_JOBB)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `skal ikke svare på spm om siste stilling hvis vi allerede vet at bruker har hatt jobb`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.MISTET_JOBBEN)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
        )
        ValideringUtils.validerBrukerRegistrering(ordinaerBrukerRegistrering)
        ordinaerBrukerRegistrering.setBesvarelse(
            ordinaerBrukerRegistrering.getBesvarelse()
                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
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
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setBesvarelse(
            gyldigBesvarelse().setAndreForhold(null)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }

    @Test
    fun `validering skal feile hvis stilling har nullfelt`() {
        val ordinaerBrukerRegistrering = gyldigBrukerRegistrering().setSisteStilling(
            gyldigStilling(label = null)
        )
        assertThrows<RuntimeException> {
            ValideringUtils.validerBrukerRegistrering(
                ordinaerBrukerRegistrering
            )
        }
    }
}
