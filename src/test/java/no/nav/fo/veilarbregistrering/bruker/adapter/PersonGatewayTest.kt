package no.nav.fo.veilarbregistrering.bruker.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.finn.unleash.UnleashContext
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.HealthCheckResult
import no.nav.fo.veilarbregistrering.bruker.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import java.util.*
import javax.servlet.http.HttpServletRequest

class PersonGatewayTest {

    private fun lagPdlOppslagGateway(gt: GeografiskTilknytning? = null): PdlOppslagGateway {
        val pdlOppslagGatewayMock = mockk<PdlOppslagGateway>()
        every { pdlOppslagGatewayMock.hentGeografiskTilknytning(any())} returns Optional.ofNullable(gt)
        return pdlOppslagGatewayMock
    }

    @Test
    fun `hentGeografiskTilknytning skal returnere geografisk tilknytning fra PDL`() {
        val foedselsnummer = Foedselsnummer.of("12345678910")
        val bruker = Bruker.of(foedselsnummer, null)
        val forventetGeografiskTilknytning = "1234"

        val personGateway = PersonGatewayImpl(lagPdlOppslagGateway(GeografiskTilknytning.of(forventetGeografiskTilknytning)))

        val geografiskTilknytning = personGateway.hentGeografiskTilknytning(bruker)
        assertThat(geografiskTilknytning).hasValue(GeografiskTilknytning.of(forventetGeografiskTilknytning))
    }
}
