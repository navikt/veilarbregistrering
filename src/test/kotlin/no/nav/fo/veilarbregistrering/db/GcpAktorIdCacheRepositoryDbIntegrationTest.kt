package no.nav.fo.veilarbregistrering.db

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCache
import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
@ActiveProfiles("gcp")
class GcpAktorIdCacheRepositoryDbIntegrationTest(
    @Autowired
    private val aktorIdCacheRepository: AktorIdCacheRepository
) {

    @Test
    fun `skal hente aktørId basert på fødselsnummer`() {
        val FOEDSELSNUMMER = Foedselsnummer("01234567890")
        val AKTORID = AktorId("1000010000100")

        aktorIdCacheRepository.lagre(AktorIdCache(FOEDSELSNUMMER, AKTORID, LocalDateTime.now()))

        val aktorIdCache = aktorIdCacheRepository.hentAktørId(FOEDSELSNUMMER)
        assertNotNull(aktorIdCache)
        assertEquals(AKTORID, aktorIdCache.aktorId)
    }
}