package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.DownstreamApi
import no.nav.fo.veilarbregistrering.autentisering.tokenveksling.TokenExchangeService
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PdlOppslagConfig {

    @Bean
    fun pdlOppslagClient(tokenExchangeService: TokenExchangeService): PdlOppslagClient {
        val baseUrl = requireProperty("PDL_URL")

        return PdlOppslagClient(baseUrl) {
            tokenExchangeService.exchangeToken(pdlApi)
        }
    }

    @Bean
    fun pdlOppslagGateway(pdlOppslagClient: PdlOppslagClient): PdlOppslagGateway {
        return PdlOppslagGatewayImpl(pdlOppslagClient)
    }
}

val pdlApi = DownstreamApi(requireProperty("PDL_CLUSTER"), "pdl", "pdl-api")
