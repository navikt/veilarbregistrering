package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortPeriode
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.db.RepositoryConfig
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.time.LocalDateTime

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
    initializers = [DbContainerInitializer::class],
    classes = [RepositoryConfig::class, DatabaseConfig::class]
)
@ActiveProfiles("gcp")
internal class MeldekortRepositoryImplTest(@Autowired private val meldekortRepository: MeldekortRepository) {
    @Test
    fun lagre() {
        val meldekort = MeldekortEvent(
            Foedselsnummer("12345678911"),
            true,
            MeldekortPeriode(
                LocalDate.now(),
                LocalDate.now()
            ),
            Meldekorttype.MANUELL_ARENA,
            123,
            LocalDateTime.now()
        )

        meldekortRepository.lagre(meldekort)
    }

    @Test
    fun hent() {
    }
}
