package no.nav.fo.veilarbregistrering.feil

import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR


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
}