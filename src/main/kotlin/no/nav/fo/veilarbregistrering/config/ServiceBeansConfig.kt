package no.nav.fo.veilarbregistrering.config

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeMottakService
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortMottakService
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerService
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperiodeAvsluttetProducer
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerperiodeAvsluttetService
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.resources.ArbeidssokerResource
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.bruker.resources.InternalUserResource
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.featuretoggle.resources.FeaturetoggleResource
import no.nav.fo.veilarbregistrering.feil.FeilHandtering
import no.nav.fo.veilarbregistrering.helsesjekk.resources.HelsesjekkResource
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateClient
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateRepository
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateService
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrationStatusService
import no.nav.fo.veilarbregistrering.migrering.konsument.resources.StatusController
import no.nav.fo.veilarbregistrering.migrering.tilbyder.MigreringRepository
import no.nav.fo.veilarbregistrering.migrering.tilbyder.resources.MigreringResource
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRouter
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.profilering.ProfilertInnsatsgruppeService
import no.nav.fo.veilarbregistrering.profilering.resources.ProfileringApi
import no.nav.fo.veilarbregistrering.profilering.resources.ProfileringResource
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringStatusService
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.RegistreringResource
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandService
import no.nav.fo.veilarbregistrering.registrering.formidling.resources.InternalRegistreringStatusoversiktResource
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.resources.OrdinaerBrukerRegistreringResource
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.resources.ReaktiveringResource
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringService
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.resources.SykmeldtResource
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.veileder.NavVeilederService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceBeansConfig {

    @Bean
    fun hentRegistreringService(
        brukerRegistreringRepository: BrukerRegistreringRepository,
        sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
        profileringRepository: ProfileringRepository,
        manuellRegistreringRepository: ManuellRegistreringRepository,
        norg2Gateway: Norg2Gateway,
        metricsService: MetricsService
    ): HentRegistreringService {
        return HentRegistreringService(
            brukerRegistreringRepository,
            sykmeldtRegistreringRepository,
            profileringRepository,
            manuellRegistreringRepository,
            norg2Gateway,
            metricsService
        )
    }

    @Bean
    fun navVeilederService(tilgangskontrollService: TilgangskontrollService, userService: UserService): NavVeilederService {
        return NavVeilederService(tilgangskontrollService, userService)
    }

    @Bean
    fun registreringTilstandService(registreringTilstandRepository: RegistreringTilstandRepository): RegistreringTilstandService {
        return RegistreringTilstandService(registreringTilstandRepository)
    }

    @Bean
    fun brukerTilstandService(oppfolgingGateway: OppfolgingGateway): BrukerTilstandService {
        return BrukerTilstandService(oppfolgingGateway)
    }

    @Bean
    fun startRegistreringStatusService(
        arbeidsforholdGateway: ArbeidsforholdGateway,
        brukerTilstandService: BrukerTilstandService,
        pdlOppslagGateway: PdlOppslagGateway,
        metricsService: MetricsService
    ): StartRegistreringStatusService {
        return StartRegistreringStatusService(
            arbeidsforholdGateway,
            brukerTilstandService,
            pdlOppslagGateway,
            metricsService
        )
    }

    @Bean
    fun reaktiveringBrukerService(
        brukerTilstandService: BrukerTilstandService,
        reaktiveringRepository: ReaktiveringRepository,
        oppfolgingGateway: OppfolgingGateway,
        metricsService: MetricsService
    ): ReaktiveringBrukerService {
        return ReaktiveringBrukerService(
            brukerTilstandService,
            reaktiveringRepository,
            oppfolgingGateway,
            metricsService
        )
    }

    @Bean
    fun sykmeldtRegistreringService(
        brukerTilstandService: BrukerTilstandService,
        oppfolgingGateway: OppfolgingGateway,
        sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
        manuellRegistreringRepository: ManuellRegistreringRepository,
        metricsService: MetricsService
    ): SykmeldtRegistreringService {
        return SykmeldtRegistreringService(
            brukerTilstandService,
            oppfolgingGateway,
            sykmeldtRegistreringRepository,
            manuellRegistreringRepository,
            metricsService
        )
    }

    @Bean
    fun registrerBrukerService(
        brukerRegistreringRepository: BrukerRegistreringRepository,
        profileringRepository: ProfileringRepository,
        oppfolgingGateway: OppfolgingGateway,
        profileringService: ProfileringService,
        registreringTilstandRepository: RegistreringTilstandRepository,
        brukerTilstandService: BrukerTilstandService,
        manuellRegistreringRepository: ManuellRegistreringRepository,
        metricsService: MetricsService
    ): BrukerRegistreringService {
        return BrukerRegistreringService(
            brukerRegistreringRepository,
            profileringRepository,
            oppfolgingGateway,
            profileringService,
            registreringTilstandRepository,
            brukerTilstandService,
            manuellRegistreringRepository,
            metricsService
        )
    }

    @Bean
    fun sykmeldtResource(
        tilgangskontrollService: TilgangskontrollService,
        userService: UserService,
        unleashClient: UnleashClient,
        sykmeldtRegistreringService: SykmeldtRegistreringService,
        navVeilederService: NavVeilederService,
    ) : SykmeldtResource {
        return SykmeldtResource(
            tilgangskontrollService,
            userService,
            unleashClient,
            sykmeldtRegistreringService,
            navVeilederService)
    }

    @Bean
    fun reaktiveringResource(
        userService: UserService,
        unleashClient: UnleashClient,
        tilgangskontrollService: TilgangskontrollService,
        reaktiveringBrukerService: ReaktiveringBrukerService
    ) : ReaktiveringResource {
        return ReaktiveringResource(
            userService,
            unleashClient,
            tilgangskontrollService,
            reaktiveringBrukerService
        )
    }

    @Bean
    fun ordinaerBrukerRegistrering(
        tilgangskontrollService: TilgangskontrollService,
        userService: UserService,
        brukerRegistreringService: BrukerRegistreringService,
        navVeilederService: NavVeilederService,
        unleashClient: UnleashClient
    ): OrdinaerBrukerRegistreringResource {
        return OrdinaerBrukerRegistreringResource(
            tilgangskontrollService,
            userService,
            brukerRegistreringService,
            navVeilederService,
            unleashClient
        )
    }

    @Bean
    fun registreringResource(
        tilgangskontrollService: TilgangskontrollService,
        userService: UserService,
        hentRegistreringService: HentRegistreringService,
        startRegistreringStatusService: StartRegistreringStatusService
    ): RegistreringResource {
        return RegistreringResource(
            tilgangskontrollService,
            userService,
            hentRegistreringService,
            startRegistreringStatusService
        )
    }

    @Bean
    fun helsesjekkResource(selfTestChecks: SelfTestChecks): HelsesjekkResource {
        return HelsesjekkResource(selfTestChecks)
    }

    @Bean
    fun featuretoggleResource(unleashClient: UnleashClient): FeaturetoggleResource {
        return FeaturetoggleResource(unleashClient)
    }

    @Bean
    fun arbeidsforholdResource(
        userService: UserService,
        tilgangskontrollService: TilgangskontrollService,
        arbeidsforholdGateway: ArbeidsforholdGateway
    ): ArbeidsforholdResource {
        return ArbeidsforholdResource(
            userService,
            tilgangskontrollService,
            arbeidsforholdGateway
        )
    }

    @Bean
    fun oppgaveService(
        oppgaveGateway: OppgaveGateway,
        oppgaveRepository: OppgaveRepository,
        oppgaveRouter: OppgaveRouter,
        metricsService: MetricsService
    ): OppgaveService {
        return OppgaveService(
            oppgaveGateway,
            oppgaveRepository,
            oppgaveRouter,
            metricsService
        )
    }

    @Bean
    fun oppgaveRouter(
        arbeidsforholdGateway: ArbeidsforholdGateway,
        enhetGateway: EnhetGateway,
        norg2Gateway: Norg2Gateway,
        pdlOppslagGateway: PdlOppslagGateway,
        metricsService: MetricsService
    ): OppgaveRouter {
        return OppgaveRouter(
            arbeidsforholdGateway,
            enhetGateway,
            norg2Gateway,
            pdlOppslagGateway,
            metricsService
        )
    }

    @Bean
    fun oppgaveResource(
        userService: UserService,
        oppgaveService: OppgaveService,
        tilgangskontrollService: TilgangskontrollService
    ): OppgaveResource {
        return OppgaveResource(userService, oppgaveService, tilgangskontrollService)
    }

    @Bean
    fun arbeidssokerService(
        formidlingsgruppeRepository: FormidlingsgruppeRepository,
        formidlingsgruppeGateway: FormidlingsgruppeGateway,
        unleashClient: UnleashClient,
        metricsService: MetricsService
    ): ArbeidssokerService {
        return ArbeidssokerService(
            formidlingsgruppeRepository,
            formidlingsgruppeGateway,
            unleashClient,
            metricsService
        )
    }

    @Bean
    fun formidlingsgruppeMottakService(
        formidlingsgruppeRepository: FormidlingsgruppeRepository,
        arbeidssokerperiodeAvsluttetService: ArbeidssokerperiodeAvsluttetService
    ): FormidlingsgruppeMottakService {
        return FormidlingsgruppeMottakService(formidlingsgruppeRepository, arbeidssokerperiodeAvsluttetService)
    }

    @Bean
    fun arbeidssokerperiodeAvsluttetService(arbeidssokerperiodeAvsluttetProducer: ArbeidssokerperiodeAvsluttetProducer): ArbeidssokerperiodeAvsluttetService =
        ArbeidssokerperiodeAvsluttetService(arbeidssokerperiodeAvsluttetProducer)

    @Bean
    fun arbeidssokerperiodeAvsluttetProducer(): ArbeidssokerperiodeAvsluttetProducer = ArbeidssokerperiodeAvsluttetProducer()

    @Bean
    fun arbeidssokerResource(
        arbeidssokerService: ArbeidssokerService,
        userService: UserService,
        tilgangskontrollService: TilgangskontrollService
    ): ArbeidssokerResource {
        return ArbeidssokerResource(arbeidssokerService, userService, tilgangskontrollService)
    }

    @Bean
    fun publiseringAvEventsService(
        profileringRepository: ProfileringRepository,
        brukerRegistreringRepository: BrukerRegistreringRepository,
        registrertProducer: ArbeidssokerRegistrertProducer,
        registreringTilstandRepository: RegistreringTilstandRepository,
        profilertProducer: ArbeidssokerProfilertProducer,
        metricsService: MetricsService
    ): PubliseringAvEventsService {
        return PubliseringAvEventsService(
            profileringRepository,
            brukerRegistreringRepository,
            registrertProducer,
            registreringTilstandRepository,
            profilertProducer,
            metricsService
        )
    }

    @Bean
    fun profileringService(arbeidsforholdGateway: ArbeidsforholdGateway): ProfileringService {
        return ProfileringService(arbeidsforholdGateway)
    }

    @Bean
    fun profilertInnsatsgruppeService(
        oppfolgingGateway: OppfolgingGateway,
        profileringRepository: ProfileringRepository,
        brukerRegistreringRepository: BrukerRegistreringRepository
    ): ProfilertInnsatsgruppeService {
        return ProfilertInnsatsgruppeService(oppfolgingGateway, profileringRepository, brukerRegistreringRepository)
    }

    @Bean
    fun profileringResource(
        userService: UserService,
        tilgangskontrollService: TilgangskontrollService,
        profilertInnsatsgruppeService: ProfilertInnsatsgruppeService
    ): ProfileringApi {
        return ProfileringResource(userService, tilgangskontrollService, profilertInnsatsgruppeService)
    }

    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway, authContextHolder: AuthContextHolder): UserService {
        return UserService(pdlOppslagGateway, authContextHolder, isDevelopment())
    }

    @Bean
    fun kontaktinfoService(
        pdlOppslagGateway: PdlOppslagGateway,
        krrGateway: KrrGateway
    ): KontaktinfoService {
        return KontaktinfoService(pdlOppslagGateway, krrGateway)
    }

    @Bean
    fun kontaktinfoResource(
        userService: UserService,
        kontaktinfoService: KontaktinfoService,
        tilgangskontrollService: TilgangskontrollService
    ): KontaktinfoResource {
        return KontaktinfoResource(userService, kontaktinfoService, tilgangskontrollService)
    }

    @Bean
    fun internalUserResource(userService: UserService) = InternalUserResource(userService)

    @Bean
    fun internalRegistreringTilstandServlet(registreringTilstandService: RegistreringTilstandService): InternalRegistreringStatusoversiktResource {
        return InternalRegistreringStatusoversiktResource(registreringTilstandService)
    }

    @Bean
    fun feilHandtering(): FeilHandtering {
        return FeilHandtering()
    }

    @Bean
    fun migreringPostgressResource(migreringRepository: MigreringRepository): MigreringResource {
        return MigreringResource(migreringRepository)
    }

    @Bean
    fun migrationStatusService(migrateClient: MigrateClient, migrateRepository: MigrateRepository): MigrationStatusService {
        return MigrationStatusService(migrateClient, migrateRepository)
    }

    @Bean
    fun statusController(migrationStatusService: MigrationStatusService): StatusController {
        return StatusController(migrationStatusService)
    }

    @Bean
    fun migrateService(repository: MigrateRepository, migrateClient: MigrateClient): MigrateService {
        return MigrateService(repository, migrateClient)
    }

    @Bean
    fun meldekortMottakService(meldekortRepository: MeldekortRepository): MeldekortMottakService {
        return MeldekortMottakService(meldekortRepository)
    }
}
