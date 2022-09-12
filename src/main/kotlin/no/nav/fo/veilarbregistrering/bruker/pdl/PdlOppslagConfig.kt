package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PdlOppslagConfig {

    @Bean
    fun pdlOppslagClient(machineToMachineTokenClient: AzureAdMachineToMachineTokenClient): PdlOppslagClient {
        val baseUrl = requireProperty("PDL_URL")
        val pdlCluster = requireProperty("PDL_CLUSTER")

        return PdlOppslagClient(baseUrl) {
            machineToMachineTokenClient.createMachineToMachineToken("api://$pdlCluster.pdl.pdl-api/.default")
        }
    }

    @Bean
    fun pdlOppslagGateway(pdlOppslagClient: PdlOppslagClient): PdlOppslagGateway {
        return PdlOppslagGatewayImpl(pdlOppslagClient)
    }
}
