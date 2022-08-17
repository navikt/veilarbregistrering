package no.nav.fo.veilarbregistrering.config

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource
import no.nav.fo.veilarbregistrering.arbeidssoker.*
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.ArbeidssokerResource
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.bruker.resources.InternalUserResource
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource
import no.nav.fo.veilarbregistrering.db.migrering.MigreringRepositoryImpl
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.featuretoggle.resources.FeaturetoggleResource
import no.nav.fo.veilarbregistrering.feil.FeilHandtering
import no.nav.fo.veilarbregistrering.helsesjekk.resources.HelsesjekkResource
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.migrering.resources.MigreringResource
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
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraRepository
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraService
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources.GjelderFraDatoResource
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
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
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeAggregator
import no.nav.fo.veilarbregistrering.tidslinje.resources.TidslinjeResource
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
    fun registreringTilstandService(registreringTilstandRepository: RegistreringTilstandRepository): RegistreringTilstandService {
        return RegistreringTilstandService(registreringTilstandRepository)
    }

    @Bean
    fun brukerTilstandService(
        oppfolgingGateway: OppfolgingGateway,
        brukerRegistreringRepository: BrukerRegistreringRepository
    ): BrukerTilstandService {
        return BrukerTilstandService(oppfolgingGateway, brukerRegistreringRepository)
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
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        unleashClient: UnleashClient,
        sykmeldtRegistreringService: SykmeldtRegistreringService
    ) : SykmeldtResource {
        return SykmeldtResource(
            autorisasjonService,
            userService,
            unleashClient,
            sykmeldtRegistreringService)
    }

    @Bean
    fun reaktiveringResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        unleashClient: UnleashClient,
        tilgangskontrollService: TilgangskontrollService,
        reaktiveringBrukerService: ReaktiveringBrukerService
    ) : ReaktiveringResource {
        return ReaktiveringResource(
            autorisasjonService,
            userService,
            unleashClient,
            tilgangskontrollService,
            reaktiveringBrukerService
        )
    }

    @Bean
    fun ordinaerBrukerRegistrering(
        autorisasjonsService: AutorisasjonService,
        userService: UserService,
        brukerRegistreringService: BrukerRegistreringService,
        unleashClient: UnleashClient
    ): OrdinaerBrukerRegistreringResource {
        return OrdinaerBrukerRegistreringResource(
            autorisasjonsService,
            userService,
            brukerRegistreringService,
            unleashClient
        )
    }

    @Bean
    fun registreringResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        hentRegistreringService: HentRegistreringService,
        startRegistreringStatusService: StartRegistreringStatusService
    ): RegistreringResource {
        return RegistreringResource(
            autorisasjonService,
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
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        unleashClient: UnleashClient,
        tilgangskontrollService: TilgangskontrollService,
        arbeidsforholdGateway: ArbeidsforholdGateway
    ): ArbeidsforholdResource {
        return ArbeidsforholdResource(
            autorisasjonService,
            userService,
            unleashClient,
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
        autorisasjonService: AutorisasjonService
    ): OppgaveResource {
        return OppgaveResource(userService, oppgaveService, autorisasjonService)
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
        autorisasjonService: AutorisasjonService
    ): ArbeidssokerResource {
        return ArbeidssokerResource(arbeidssokerService, userService, autorisasjonService)
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
        autorisasjonService: AutorisasjonService,
        profilertInnsatsgruppeService: ProfilertInnsatsgruppeService
    ): ProfileringApi {
        return ProfileringResource(userService, autorisasjonService, profilertInnsatsgruppeService)
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
        autorisasjonService: AutorisasjonService
    ): KontaktinfoResource {
        return KontaktinfoResource(userService, kontaktinfoService, autorisasjonService)
    }

    @Bean
    fun tidslinjeAggregator(
        brukerRegistreringRepository: BrukerRegistreringRepository,
        sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
        reaktiveringRepository: ReaktiveringRepository,
        formidlingsgruppeRepository: FormidlingsgruppeRepository
    ): TidslinjeAggregator {
        return TidslinjeAggregator(
            brukerRegistreringRepository,
            sykmeldtRegistreringRepository,
            reaktiveringRepository,
            formidlingsgruppeRepository
        )
    }

    @Bean
    fun tidslinjeResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        tidslinjeAggregator: TidslinjeAggregator
    ): TidslinjeResource {
        return TidslinjeResource(autorisasjonService, userService, tidslinjeAggregator)
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
    fun migreringPostgressResource(
        migreringRepository: MigreringRepositoryImpl,
        brukerRegistreringRepository: BrukerRegistreringRepository,
        registreringTilstandRepository: RegistreringTilstandRepository,
    ): MigreringResource {
        return MigreringResource(
            migreringRepository,
            brukerRegistreringRepository,
            registreringTilstandRepository
        )
    }

    @Bean
    fun gjelderFraService(gjelderFraRepository: GjelderFraRepository, brukerRegistreringRepository: BrukerRegistreringRepository): GjelderFraService {
        return GjelderFraService(gjelderFraRepository, brukerRegistreringRepository)
    }

    @Bean
    fun gjelderFraDatoResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        gjelderFraService: GjelderFraService
    ): GjelderFraDatoResource {
        return GjelderFraDatoResource(autorisasjonService, userService, gjelderFraService)
    }
}
