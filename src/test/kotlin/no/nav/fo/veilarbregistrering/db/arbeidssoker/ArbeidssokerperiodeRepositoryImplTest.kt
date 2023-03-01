package no.nav.fo.veilarbregistrering.db.arbeidssoker

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
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
        assertNotEquals(null, arbeidssokerperioder.first().til)
    }
}