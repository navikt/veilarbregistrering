package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AktorIdCacheConfig {

    @Bean
    fun aktorIdCacheService(
        pdlOppslagGateway: PdlOppslagGateway,
        aktorIdCacheRepository: AktorIdCacheRepository,
    ): AktorIdCacheService {
        return AktorIdCacheService(
            pdlOppslagGateway,
            aktorIdCacheRepository
        )
    }

}