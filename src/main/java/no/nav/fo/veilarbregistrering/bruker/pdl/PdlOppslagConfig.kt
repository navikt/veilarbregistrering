package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider

import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PdlOppslagConfig {

    @Bean
    fun pdlOppslagClient(serviceToServiceTokenProvider: ServiceToServiceTokenProvider): PdlOppslagClient {
        val baseUrl = requireProperty("PDL_URL")
        val pdlCluster = requireProperty("PDL_CLUSTER")

        return PdlOppslagClient(baseUrl) {
            serviceToServiceTokenProvider
                .getServiceToken("pdl-api", "pdl", pdlCluster)
        }
    }

    @Bean
    fun pdlOppslagGateway(pdlOppslagClient: PdlOppslagClient): PdlOppslagGateway {
        return PdlOppslagGatewayImpl(pdlOppslagClient)
    }
}
