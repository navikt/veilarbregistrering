package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import no.nav.fo.veilarbregistrering.config.db.DatabaseConfig
import no.nav.fo.veilarbregistrering.config.db.RepositoryConfig
import no.nav.veilarbregistrering.integrasjonstest.db.DbContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
    initializers = [DbContainerInitializer::class],
    classes = [RepositoryConfig::class, DatabaseConfig::class]
)
internal class ArbeidssokerperiodeRepositoryImplTest(@Autowired private val arbeidssokerperiodeRepositoryImpl: ArbeidssokerperiodeRepositoryImpl) {

    @Test
    fun startPeriode() {
        arbeidssokerperiodeRepositoryImpl.startPeriode(aremark(), LocalDateTime.now())
        val arbeidssokerperioder = arbeidssokerperiodeRepositoryImpl.hentPerioder(aremark())
        assertEquals(1, arbeidssokerperioder.size)
        assertEquals(null, arbeidssokerperioder.first().til)
    }

    @Test
    fun avsluttPeriode() {
        arbeidssokerperiodeRepositoryImpl.startPeriode(aremark(), LocalDateTime.now())
        arbeidssokerperiodeRepositoryImpl.avsluttPeriode(aremark(), LocalDateTime.now())
        val arbeidssokerperioder = arbeidssokerperiodeRepositoryImpl.hentPerioder(aremark())
        assertNotNull(arbeidssokerperioder.first().til)
    }

    @Test
    fun `skal avslutte periode for id`() {
        val gjeldendeFnr = aremark()
        val start = LocalDateTime.now().minusYears(3)
        val slutt = LocalDateTime.now()
        arbeidssokerperiodeRepositoryImpl.startPeriode(gjeldendeFnr, start)
        val aktivPeriode = arbeidssokerperiodeRepositoryImpl.hentPerioder(gjeldendeFnr, emptyList())
        arbeidssokerperiodeRepositoryImpl.avsluttPeriode(aktivPeriode.first().id, slutt)
        val arbeidssokerperioder = arbeidssokerperiodeRepositoryImpl.hentPerioder(gjeldendeFnr, emptyList())

        assertEquals(1, arbeidssokerperioder.size)
        assertEquals(start.truncatedTo(ChronoUnit.MILLIS), arbeidssokerperioder.first().fra.truncatedTo(ChronoUnit.MILLIS))
        assertEquals(slutt.truncatedTo(ChronoUnit.MILLIS), arbeidssokerperioder.first().til?.truncatedTo(ChronoUnit.MILLIS))
    }

    @Test
    fun `skal hente alle perioder for en person med historiske og gjeldende fødselsnummer`() {
        val historiskeFoedselsnummer = listOf(Foedselsnummer("12345678911"), Foedselsnummer("12345678910"))
        val bruker = Bruker(aremark(), AktorId("1234"), historiskeFoedselsnummer)
        arbeidssokerperiodeRepositoryImpl.startPeriode(historiskeFoedselsnummer.first(), LocalDateTime.now().minusYears(5))
        arbeidssokerperiodeRepositoryImpl.avsluttPeriode(historiskeFoedselsnummer.first(), LocalDateTime.now().minusYears(5).plusMonths(7))

        arbeidssokerperiodeRepositoryImpl.startPeriode(historiskeFoedselsnummer.last(), LocalDateTime.now().minusYears(3))
        arbeidssokerperiodeRepositoryImpl.avsluttPeriode(historiskeFoedselsnummer.last(), LocalDateTime.now().minusYears(3).plusMonths(7))

        arbeidssokerperiodeRepositoryImpl.startPeriode(aremark(), LocalDateTime.now().minusDays(21))

        val arbeidssokerperioder = arbeidssokerperiodeRepositoryImpl.hentPerioder(bruker.gjeldendeFoedselsnummer, bruker.historiskeFoedselsnummer)

        assertEquals(3, arbeidssokerperioder.size)

    }
}