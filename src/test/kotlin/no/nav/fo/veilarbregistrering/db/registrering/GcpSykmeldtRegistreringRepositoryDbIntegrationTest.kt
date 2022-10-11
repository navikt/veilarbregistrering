package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringTestdataBuilder
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
@ActiveProfiles("gcp")
class GcpSykmeldtRegistreringRepositoryDbIntegrationTest(

    @Autowired
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository
) {
    init {
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

    @Test
    fun hentSykmeldtregistreringForAktorId() {
        val bruker1 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING))
        val bruker2 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_REDUSERT_STILLING))
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker1, AKTOR_ID_11111)
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker2, AKTOR_ID_11111)
        val registrering = sykmeldtRegistreringRepository.hentSykmeldtregistreringForAktorId(AKTOR_ID_11111)
        assertSykmeldtRegistrertBruker(bruker2, registrering!!)
    }

    @Test
    fun `finn liste med alle sykmeldt registreringer for gitt aktørId`() {
        val bruker1 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING))
        val bruker2 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_REDUSERT_STILLING))
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker1, AKTOR_ID_11111)
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker2, AKTOR_ID_11111)

        val ordinaerBrukerregistreringer = sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(AKTOR_ID_11111)
        assertThat(ordinaerBrukerregistreringer).hasSize(2)
    }

    private fun assertSykmeldtRegistrertBruker(bruker: SykmeldtRegistrering, sykmeldtRegistrering: SykmeldtRegistrering) {
        assertThat(sykmeldtRegistrering.besvarelse).isEqualTo(bruker.besvarelse)
        assertThat(sykmeldtRegistrering.teksterForBesvarelse).isEqualTo(bruker.teksterForBesvarelse)
    }

    companion object {
        private val AKTOR_ID_11111 = AktorId("11111")
    }

}

