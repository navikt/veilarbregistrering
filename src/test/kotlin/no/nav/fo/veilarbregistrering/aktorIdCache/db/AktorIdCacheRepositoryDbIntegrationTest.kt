package no.nav.fo.veilarbregistrering.aktorIdCache.db

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCache
import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class AktorIdCacheRepositoryDbIntegrationTest(
    @Autowired
    private val aktorIdCacheRepository: AktorIdCacheRepository
) {

    @Test
    fun `skal hente aktørId basert på fødselsnummer`() {
        val FOEDSELSNUMMER = Foedselsnummer("01234567890")
        val AKTORID = AktorId("1000010000100")

        aktorIdCacheRepository.lagre(AktorIdCache(FOEDSELSNUMMER, AKTORID, LocalDate.now().atStartOfDay()))

        val aktorIdCache = aktorIdCacheRepository.hentAktørId(FOEDSELSNUMMER)
        assertNotNull(aktorIdCache)
        assertEquals(AKTORID, aktorIdCache.aktorId)
    }

    @Test
    fun `få null hvis aktorId ikke finnes`() {
        val FOEDSELSNUMMER = Foedselsnummer("01234567890")
        val aktorIdCache = aktorIdCacheRepository.hentAktørId(FOEDSELSNUMMER)
        assertNull(aktorIdCache)
    }

    @Test
    fun `ikke gjøre endring ved insetting av eksisterende bruker`(){
        val opprettetDatoOriginal = LocalDate.now().atStartOfDay().minusDays(1)
        val opprettetDatoNy = LocalDate.now().atStartOfDay()

        val FOEDSELSNUMMER = Foedselsnummer("01234567890")
        val AKTORID = AktorId("1000010000100")
        aktorIdCacheRepository.lagre(AktorIdCache(FOEDSELSNUMMER, AKTORID, opprettetDatoOriginal))
        aktorIdCacheRepository.lagre(AktorIdCache(FOEDSELSNUMMER, AKTORID, opprettetDatoNy))

        val person = aktorIdCacheRepository.hentAktørId(FOEDSELSNUMMER)
        assertNotNull(person)
        assertEquals(opprettetDatoOriginal.truncatedTo(ChronoUnit.SECONDS), person.opprettetDato.truncatedTo(ChronoUnit.SECONDS))

    }



}