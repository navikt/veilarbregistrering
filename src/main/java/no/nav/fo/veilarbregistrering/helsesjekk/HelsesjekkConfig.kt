package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.abac.Pep
import no.nav.common.featuretoggle.UnleashService
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestMeterBinder
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AaregRestClient
import no.nav.fo.veilarbregistrering.bruker.krr.KrrClient
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig.PDL_PROPERTY_NAME
import no.nav.fo.veilarbregistrering.db.DatabaseHelsesjekk
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelsesjekkConfig {

    @Bean
    fun selfTestChecks(
            dbHelsesjekk: DatabaseHelsesjekk,
            veilarbPep: Pep,
            unleashService: UnleashService,
            oppfolgingClient: OppfolgingClient,
            sykmeldtInfoClient: SykmeldtInfoClient,
            krrClient: KrrClient,
            aaregRestClient: AaregRestClient
    ): SelfTestChecks {
        val pdlPingUrl = getRequiredProperty(PDL_PROPERTY_NAME)

        val selfTestChecks = listOf(
                SelfTestCheck("Enkel spørring mot Databasen til veilarregistrering.", true, dbHelsesjekk),
                SelfTestCheck("ABAC tilgangskontroll - ping", true, veilarbPep.abacClient),
                SelfTestCheck("Sjekker at feature-toggles kan hentes fra Unleash", false, unleashService),
                SelfTestCheck("Ping Oppfølging", false, oppfolgingClient),
                //TODO: Sjekk om dette fikser SelfTestCheck("Ping Pdl", false, healthCheck(pdlPingUrl, true)),
                SelfTestCheck("Ping FO Infotrygd", false, sykmeldtInfoClient),
                SelfTestCheck("Ping Kontakt og reservasjonsregisteret (KRR)", false, krrClient),
                SelfTestCheck("Ping AaReg", false, aaregRestClient)
        )
        return SelfTestChecks(selfTestChecks)
    }

    @Bean
    fun selfTestMeterBinder(selfTestChecks: SelfTestChecks): SelfTestMeterBinder {
        return SelfTestMeterBinder(selfTestChecks)
    }
}