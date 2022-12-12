package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
@ActiveProfiles("gcp")
class GcpReaktiveringRepositoryDbIntegrationTest(

    @Autowired
    private val jdbcTemplate: JdbcTemplate,

    @Autowired
    private val reaktiveringRepository: ReaktiveringRepository
) {
    init {
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

    @Test
    fun `finnReaktiveringer skal returnere liste med alle reaktiveringer for gitt aktørId`() {
        assertThat(reaktiveringRepository.finnReaktiveringer(BRUKER_1.aktorId)).hasSize(0)

        reaktiveringRepository.lagreReaktiveringForBruker(BRUKER_1)

        assertThat(reaktiveringRepository.finnReaktiveringer(BRUKER_1.aktorId)).hasSize(1)
    }

    companion object {
        private val FOEDSELSNUMMER = Foedselsnummer("12345678911")
        private val AKTOR_ID_11111 = AktorId("11111")
        private val BRUKER_1 = Bruker(FOEDSELSNUMMER, AKTOR_ID_11111)
        private val BRUKER_2 = Bruker(FoedselsnummerTestdataBuilder.aremark(), AktorId("22222"))
    }
}