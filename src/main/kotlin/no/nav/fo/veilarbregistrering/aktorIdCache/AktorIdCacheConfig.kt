package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AktorIdCacheConfig {
    @Bean
    fun populerAktorIdWorker(
        pdlOppslagGateway: PdlOppslagGateway,
        formidlingsgruppeRepository: FormidlingsgruppeRepository,
        aktorIdCacheRepository: AktorIdCacheRepository,
        unleashClient: UnleashClient
    ): PopulerAktorIdWorker {
        return PopulerAktorIdWorker(
            formidlingsgruppeRepository,
            pdlOppslagGateway,
            aktorIdCacheRepository,
            unleashClient
        )

    }
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