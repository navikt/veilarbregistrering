package no.nav.veilarbregistrering.integrasjonstest.db

import no.nav.fo.veilarbregistrering.ApplicationLocal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ApplicationLocal::class])
@ContextConfiguration(initializers = [DbContainerInitializer::class])
@ActiveProfiles("gcp")
class PostgresTest {

    @Test
    fun test1() {
        println("Hei")
    }
}