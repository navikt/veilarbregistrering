package no.nav.fo.veilarbregistrering.registrering.veileder.db

import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistrering
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DbContainerInitializer::class], classes = [ RepositoryConfig::class, DatabaseConfig::class ])
class ManuellRegistreringRepositoryDbIntegrationTest(

    @Autowired
    private val manuellRegistreringRepository: ManuellRegistreringRepository
) {
    init {
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

    @Test
    fun hentManuellRegistrering() {
        val veilederIdent = "Z1234567"
        val veilederEnhetId = "1234"
        val registreringId: Long = 1
        val registreringType = BrukerRegistreringType.ORDINAER
        val manuellRegistrering = ManuellRegistrering(
            registreringId,
            registreringType,
            veilederIdent,
            veilederEnhetId,
        )
        val id = manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering)
        val manuellRegistrering2 = ManuellRegistrering(
            id,
            manuellRegistrering.registreringId,
            manuellRegistrering.brukerRegistreringType,
            manuellRegistrering.veilederIdent,
            manuellRegistrering.veilederEnhetId
        )
        val hentetRegistrering = manuellRegistreringRepository.hentManuellRegistrering(registreringId, registreringType)
        assertEquals(manuellRegistrering2, hentetRegistrering)
    }
}