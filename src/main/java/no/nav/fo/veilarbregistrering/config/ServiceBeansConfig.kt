package no.nav.fo.veilarbregistrering.config

import no.nav.common.abac.Pep
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.ArbeidssokerResource
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService
import no.nav.fo.veilarbregistrering.bruker.KrrGateway
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.bruker.resources.InternalUserResource
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource
import no.nav.fo.veilarbregistrering.db.migrering_postgres.MigreringPostgressResource
import no.nav.fo.veilarbregistrering.db.migrering_postgres.MigreringRepositoryImpl
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway
import no.nav.fo.veilarbregistrering.feil.FeilHandtering
import no.nav.fo.veilarbregistrering.helsesjekk.resources.HelsesjekkResource
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRouter
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.RegistreringResource
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandService
import no.nav.fo.veilarbregistrering.registrering.formidling.resources.InternalRegistreringStatusoversiktResource
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.resources.SykemeldingResource
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeAggregator
import no.nav.fo.veilarbregistrering.tidslinje.resources.TidslinjeResource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceBeansConfig {
    @Bean
    fun authContextHolder(): AuthContextHolder {
        return AuthContextHolderThreadLocal.instance()
    }

    @Bean
    fun sykemeldingService(
        sykemeldingGateway: SykemeldingGateway,
        autorisasjonService: AutorisasjonService
    ): SykemeldingService {
        return SykemeldingService(sykemeldingGateway, autorisasjonService)
    }

    @Bean
    fun hentRegistreringService(
        brukerRegistreringRepository: BrukerRegistreringRepository,
        sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
        profileringRepository: ProfileringRepository,
        manuellRegistreringRepository: ManuellRegistreringRepository,
        norg2Gateway: Norg2Gateway
    ): HentRegistreringService {
        return HentRegistreringService(
            brukerRegistreringRepository,
            sykmeldtRegistreringRepository,
            profileringRepository,
            manuellRegistreringRepository,
            norg2Gateway
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
        prometheusMetricsService: PrometheusMetricsService
    ): StartRegistreringStatusService {
        return StartRegistreringStatusService(
            arbeidsforholdGateway,
            brukerTilstandService,
            pdlOppslagGateway,
            prometheusMetricsService
        )
    }

    @Bean
    fun inaktivBrukerService(
        brukerTilstandService: BrukerTilstandService,
        reaktiveringRepository: ReaktiveringRepository,
        oppfolgingGateway: OppfolgingGateway,
        prometheusMetricsService: PrometheusMetricsService
    ): InaktivBrukerService {
        return InaktivBrukerService(
            brukerTilstandService,
            reaktiveringRepository,
            oppfolgingGateway,
            prometheusMetricsService
        )
    }

    @Bean
    fun sykmeldtRegistreringService(
        brukerTilstandService: BrukerTilstandService,
        oppfolgingGateway: OppfolgingGateway,
        sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
        manuellRegistreringRepository: ManuellRegistreringRepository,
        prometheusMetricsService: PrometheusMetricsService
    ): SykmeldtRegistreringService {
        return SykmeldtRegistreringService(
            brukerTilstandService,
            oppfolgingGateway,
            sykmeldtRegistreringRepository,
            manuellRegistreringRepository,
            prometheusMetricsService
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
        prometheusMetricsService: PrometheusMetricsService
    ): BrukerRegistreringService {
        return BrukerRegistreringService(
            brukerRegistreringRepository,
            profileringRepository,
            oppfolgingGateway,
            profileringService,
            registreringTilstandRepository,
            brukerTilstandService,
            manuellRegistreringRepository,
            prometheusMetricsService
        )
    }

    @Bean
    fun registreringResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        brukerRegistreringService: BrukerRegistreringService,
        hentRegistreringService: HentRegistreringService,
        unleashClient: UnleashClient,
        startRegistreringStatusService: StartRegistreringStatusService,
        sykmeldtRegistreringService: SykmeldtRegistreringService,
        inaktivBrukerService: InaktivBrukerService
    ): RegistreringResource {
        return RegistreringResource(
            autorisasjonService,
            userService,
            brukerRegistreringService,
            hentRegistreringService,
            unleashClient,
            sykmeldtRegistreringService,
            startRegistreringStatusService,
            inaktivBrukerService
        )
    }

    @Bean
    fun helsesjekkResource(selfTestChecks: SelfTestChecks): HelsesjekkResource {
        return HelsesjekkResource(selfTestChecks)
    }

    @Bean
    fun arbeidsforholdResource(
        autorisasjonService: AutorisasjonService,
        userService: UserService,
        arbeidsforholdGateway: ArbeidsforholdGateway
    ): ArbeidsforholdResource {
        return ArbeidsforholdResource(
            autorisasjonService,
            userService,
            arbeidsforholdGateway
        )
    }

    @Bean
    fun sykemeldingResource(
        userService: UserService,
        sykemeldingService: SykemeldingService,
        autorisasjonsService: AutorisasjonService
    ): SykemeldingResource {
        return SykemeldingResource(
            userService,
            sykemeldingService,
            autorisasjonsService
        )
    }

    @Bean
    fun oppgaveService(
        oppgaveGateway: OppgaveGateway,
        oppgaveRepository: OppgaveRepository,
        oppgaveRouter: OppgaveRouter,
        prometheusMetricsService: PrometheusMetricsService
    ): OppgaveService {
        return OppgaveService(
            oppgaveGateway,
            oppgaveRepository,
            oppgaveRouter,
            prometheusMetricsService
        )
    }

    @Bean
    fun oppgaveRouter(
        arbeidsforholdGateway: ArbeidsforholdGateway,
        enhetGateway: EnhetGateway,
        norg2Gateway: Norg2Gateway,
        pdlOppslagGateway: PdlOppslagGateway,
        prometheusMetricsService: PrometheusMetricsService
    ): OppgaveRouter {
        return OppgaveRouter(
            arbeidsforholdGateway,
            enhetGateway,
            norg2Gateway,
            pdlOppslagGateway,
            prometheusMetricsService
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
        arbeidssokerRepository: ArbeidssokerRepository,
        formidlingsgruppeGateway: FormidlingsgruppeGateway,
        unleashClient: UnleashClient,
        prometheusMetricsService: PrometheusMetricsService
    ): ArbeidssokerService {
        return ArbeidssokerService(
            arbeidssokerRepository,
            formidlingsgruppeGateway,
            unleashClient,
            prometheusMetricsService
        )
    }

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
        @Qualifier("arbeidssokerRegistrertKafkaProducerAiven") arbeidssokerRegistrertProducerAiven: ArbeidssokerRegistrertProducer,
        registreringTilstandRepository: RegistreringTilstandRepository,
        @Qualifier("arbeidssokerProfilertKafkaProducerAiven") arbeidssokerProfilertProducerAiven: ArbeidssokerProfilertProducer,
        prometheusMetricsService: PrometheusMetricsService
    ): PubliseringAvEventsService {
        return PubliseringAvEventsService(
            profileringRepository,
            brukerRegistreringRepository,
            arbeidssokerRegistrertProducerAiven,
            registreringTilstandRepository,
            arbeidssokerProfilertProducerAiven,
            prometheusMetricsService
        )
    }

    @Bean
    fun profileringService(arbeidsforholdGateway: ArbeidsforholdGateway): ProfileringService {
        return ProfileringService(arbeidsforholdGateway)
    }

    @Bean
    fun userService(pdlOppslagGateway: PdlOppslagGateway, authContextHolder: AuthContextHolder): UserService {
        return UserService(pdlOppslagGateway, authContextHolder)
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
        arbeidssokerRepository: ArbeidssokerRepository
    ): TidslinjeAggregator {
        return TidslinjeAggregator(
            brukerRegistreringRepository,
            sykmeldtRegistreringRepository,
            reaktiveringRepository,
            arbeidssokerRepository
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
    fun autorisasjonService(veilarbPep: Pep, authContextHolder: AuthContextHolder): AutorisasjonService {
        return AutorisasjonService(veilarbPep, authContextHolder)
    }

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
        brukerRegistreringRepository: BrukerRegistreringRepository
    ): MigreringPostgressResource {
        return MigreringPostgressResource(migreringRepository, brukerRegistreringRepository)
    }
}