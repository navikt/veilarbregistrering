package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.abac.Pep
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestMeterBinder
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AaregRestClient
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeRestClient
import no.nav.fo.veilarbregistrering.bruker.krr.DigDirKrrProxyClient
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagClient
import no.nav.fo.veilarbregistrering.db.DatabaseHelsesjekk
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetRestClient
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveRestClient
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2RestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelsesjekkConfig {

    @Bean
    fun selfTestChecks(
            dbHelsesjekk: DatabaseHelsesjekk,
            veilarbPep: Pep,
            unleashClient: UnleashClient,
            oppfolgingClient: OppfolgingClient,
            formidlingsgruppeRestClient: FormidlingsgruppeRestClient,
            krrClient: DigDirKrrProxyClient,
            aaregRestClient: AaregRestClient,
            enhetRestClient: EnhetRestClient,
            oppgaveRestClient: OppgaveRestClient,
            norg2RestClient: Norg2RestClient,
            pdlOppslagClient: PdlOppslagClient
    ): SelfTestChecks {
        val selfTestChecks = listOf(
            SelfTestCheck("Ping (sporring) mot Databasen til veilarregistrering.", true, dbHelsesjekk),
            SelfTestCheck("Ping mot ABAC tilgangskontroll", true, veilarbPep.abacClient),
            SelfTestCheck("Ping mot Unleash (tilbyr feature-toggles)", false, unleashClient),
            SelfTestCheck("Ping Oppfolging", false, oppfolgingClient),
            SelfTestCheck("Ping Arena med ORDS-tjenesten", false, formidlingsgruppeRestClient),
            SelfTestCheck("Ping Kontakt og reservasjonsregisteret (KRR)", false, krrClient),
            SelfTestCheck("Ping Arbeid og arbeidstager registeret (Aareg)", false, aaregRestClient),
            SelfTestCheck("Ping Enhetsregisteret", false, enhetRestClient),
            SelfTestCheck("Ping Oppgave API", false, oppgaveRestClient),
            SelfTestCheck("Ping Norg2 API", false, norg2RestClient),
            SelfTestCheck("Ping PDL", false, pdlOppslagClient)
        )
        return SelfTestChecks(selfTestChecks)
    }

    @Bean
    fun selfTestAggregateMeterBinder(selfTestChecks: SelfTestChecks) = SelfTestMeterBinder(selfTestChecks)

    @Bean
    fun selfTestStatusMeterBinder(selfTestChecks: SelfTestChecks) = SelfTestStatusMeterBinder(selfTestChecks)
}
