package no.nav.fo.veilarbregistrering.feil

import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.bind.annotation.ExceptionHandler
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation


class FeilHandteringTest {
    private val feilHandtering = FeilHandtering()

    @Test
    fun `Svarer med riktig struktur dersom aktivering av bruker feiler`() {
        val entity =
            feilHandtering.aktiverBrukerException(AktiverBrukerException(BRUKER_MANGLER_ARBEIDSTILLATELSE))

        assertThat(entity.body).isNotNull
        assertThat(entity.body?.type == BRUKER_MANGLER_ARBEIDSTILLATELSE.toString())
        assertThat(entity.statusCode == INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Alle handlers returnerer en response entity`() {
        val handlers = FeilHandtering::class.declaredMemberFunctions
            .filter { it.findAnnotation<ExceptionHandler>() != null }


        handlers.forEach {
            assertThat(it.returnType.toString().contains("ResponseEntity")).describedAs("${it.name} mangler returtype").isTrue
        }
    }
}