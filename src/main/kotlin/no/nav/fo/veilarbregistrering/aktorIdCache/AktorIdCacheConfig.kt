package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
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
        unleashClient: UnleashClient,
        leaderElectionClient: LeaderElectionClient
    ): PopulerAktorIdWorker {
        return PopulerAktorIdWorker(
            formidlingsgruppeRepository,
            pdlOppslagGateway,
            aktorIdCacheRepository,
            unleashClient,
            leaderElectionClient
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