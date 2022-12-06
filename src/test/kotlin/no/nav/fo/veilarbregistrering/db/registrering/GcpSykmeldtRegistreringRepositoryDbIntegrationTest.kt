package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
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
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker1, BRUKER)
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker2, BRUKER)
        val registrering = sykmeldtRegistreringRepository.hentSykmeldtregistreringForAktorId(BRUKER.aktorId)
        assertSykmeldtRegistrertBruker(bruker2, registrering!!)
    }

    @Test
    fun `finn liste med alle sykmeldt registreringer for gitt aktørId`() {
        val bruker1 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING))
        val bruker2 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
                tilbakeIArbeid = TilbakeIArbeidSvar.JA_REDUSERT_STILLING))
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker1, BRUKER)
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker2, BRUKER)

        val ordinaerBrukerregistreringer = sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(BRUKER.aktorId)
        assertThat(ordinaerBrukerregistreringer).hasSize(2)
    }

    private fun assertSykmeldtRegistrertBruker(bruker: SykmeldtRegistrering, sykmeldtRegistrering: SykmeldtRegistrering) {
        assertThat(sykmeldtRegistrering.besvarelse).isEqualTo(bruker.besvarelse)
        assertThat(sykmeldtRegistrering.teksterForBesvarelse).isEqualTo(bruker.teksterForBesvarelse)
    }

    @Test
    fun `finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer skal returnere AktorId uten Foedselsnummer`() {
        val bruker1 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
            tilbakeIArbeid = TilbakeIArbeidSvar.JA_FULL_STILLING))
        val bruker2 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering(besvarelse = BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
            tilbakeIArbeid = TilbakeIArbeidSvar.JA_REDUSERT_STILLING))
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker1, BRUKER)
        sykmeldtRegistreringRepository.lagreSykmeldtBruker(bruker2, BRUKER)

        val aktorIdList =
            sykmeldtRegistreringRepository.finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(50)

        assertThat(aktorIdList).isEmpty()
    }

    companion object {
        private val ident = Foedselsnummer("10108000398") //Aremark fiktivt fnr.";
        private val BRUKER = Bruker(ident, AktorId("11111"))
    }

}

