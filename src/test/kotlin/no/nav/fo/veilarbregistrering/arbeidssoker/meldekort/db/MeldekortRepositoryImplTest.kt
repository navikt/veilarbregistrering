package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.db

import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortPeriode
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
    initializers = [DbContainerInitializer::class],
    classes = [RepositoryConfig::class, DatabaseConfig::class]
)
internal class MeldekortRepositoryImplTest(@Autowired private val meldekortRepository: MeldekortRepository) {

    init {
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
    }

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

        val fnr = Foedselsnummer("12345678911")
        val meldekort = MeldekortEvent(
            fnr,
            true,
            MeldekortPeriode(
                LocalDate.now(),
                LocalDate.now()
            ),
            Meldekorttype.MANUELL_ARENA,
            123,
            LocalDateTime.of(2022, 1, 1, 12, 0, 0)
        )

        meldekortRepository.lagre(meldekort)
        val melderkortEvent = meldekortRepository.hent(fnr)

        assertFalse(melderkortEvent.isEmpty())
        assertEquals(melderkortEvent.first().meldekorttype, meldekort.meldekorttype)
        assertEquals(melderkortEvent.first().fnr, meldekort.fnr)
        assertEquals(melderkortEvent.first().eventOpprettet.truncatedTo(ChronoUnit.MICROS), meldekort.eventOpprettet.truncatedTo(
            ChronoUnit.MICROS))
    }
}
