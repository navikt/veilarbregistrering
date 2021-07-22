package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PdlOppslagConfig {

    @Bean
    fun pdlOppslagClient(systemUserTokenProvider: SystemUserTokenProvider): PdlOppslagClient {
        val baseUrl = EnvironmentUtils.getRequiredProperty("PDL_URL")
        return PdlOppslagClient(baseUrl, systemUserTokenProvider)
    }

    @Bean
    fun pdlOppslagGateway(pdlOppslagClient: PdlOppslagClient): PdlOppslagGateway {
        return PdlOppslagGatewayImpl(pdlOppslagClient)
    }
}
