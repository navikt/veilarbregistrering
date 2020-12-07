package no.nav.fo.veilarbregistrering.db.profilering

import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.MigrationUtils
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.fo.veilarbregistrering.db.TransactionalTest
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration

@TransactionalTest
@ContextConfiguration(classes = [DatabaseConfig::class, RepositoryConfig::class])
open class ProfileringRepositoryDbIntegrationTest(

    @Autowired
    private val jdbcTemplate: JdbcTemplate,
    @Autowired
    private val profileringRepository: ProfileringRepository) {

    @BeforeEach
    fun setup() {
        MigrationUtils.createTables(jdbcTemplate)
    }

    @Test
    fun profilerBruker() {
        val profilering = Profilering()
                .setAlder(39)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .setInnsatsgruppe(Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING)
        profileringRepository.lagreProfilering(9, profilering)
    }
}