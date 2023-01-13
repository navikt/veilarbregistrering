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
        aktorIdCacheRepository: AktorIdCacheRepository,
        formidlingsgruppeRepository: FormidlingsgruppeRepository,
        pdlOppslagGateway: PdlOppslagGateway,
        unleashClient: UnleashClient
    ): PopulerAktorIdWorker {
        return PopulerAktorIdWorker(
            formidlingsgruppeRepository,
            pdlOppslagGateway,
            aktorIdCacheRepository,
            unleashClient
        )

    }


}