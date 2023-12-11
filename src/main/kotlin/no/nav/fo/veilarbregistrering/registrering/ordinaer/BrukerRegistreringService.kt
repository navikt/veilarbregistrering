package no.nav.fo.veilarbregistrering.registrering.ordinaer

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheService
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.secureLogger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil.Companion.fromStatus
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerTekniskException
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.Tilstandsfeil
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand.Companion.medStatus
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.formidling.Status.Companion.from
import no.nav.fo.veilarbregistrering.registrering.ordinaer.ValideringUtils.validerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistrering
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.veileder.NavVeileder
import no.nav.paw.arbeidssokerregisteret.intern.v1.OpplysningerOmArbeidssoekerMottatt
import no.nav.paw.arbeidssokerregisteret.intern.v1.vo.BrukerType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class BrukerRegistreringService(
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val profileringRepository: ProfileringRepository,
    private val oppfolgingGateway: OppfolgingGateway,
    private val profileringService: ProfileringService,
    private val registreringTilstandRepository: RegistreringTilstandRepository,
    private val brukerTilstandService: BrukerTilstandService,
    private val manuellRegistreringRepository: ManuellRegistreringRepository,
    private val metricsService: MetricsService,
    private val aktorIdCacheService: AktorIdCacheService,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService
) {
    @Transactional
    fun registrerBrukerUtenOverforing(
        ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering,
        bruker: Bruker,
        veileder: NavVeileder?
    ): OrdinaerBrukerRegistrering {

        validerBrukerRegistrering(ordinaerBrukerRegistrering, bruker)
        val opprettetBrukerRegistrering = brukerRegistreringRepository.lagre(ordinaerBrukerRegistrering, bruker)
        lagreManuellRegistrering(opprettetBrukerRegistrering, veileder)

        val profilering =
            profilerBrukerTilInnsatsgruppe(bruker.gjeldendeFoedselsnummer, opprettetBrukerRegistrering.besvarelse)
        profileringRepository.lagreProfilering(opprettetBrukerRegistrering.id, profilering)
        metricsService.registrer(Events.PROFILERING_EVENT, profilering.innsatsgruppe)

        val registreringTilstand = medStatus(Status.MOTTATT, opprettetBrukerRegistrering.id)
        registreringTilstandRepository.lagre(registreringTilstand)
        LOG.info(
            "Brukerregistrering (id: {}) gjennomført med data {}, Profilering {}",
            opprettetBrukerRegistrering.id,
            opprettetBrukerRegistrering,
            profilering
        )
        metricsService.registrer(Events.REGISTRERING_FULLFORING_REGISTRERINGSTYPE, RegistreringType.ORDINAER_REGISTRERING)

        aktorIdCacheService.settInnAktorIdHvisIkkeFinnes(bruker.gjeldendeFoedselsnummer, bruker.aktorId)

        return opprettetBrukerRegistrering
    }

    private fun validerBrukerRegistrering(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering, bruker: Bruker) {
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker)
        if (brukersTilstand.isUnderOppfolging) {
            secureLogger.warn("Bruker, ${bruker.aktorId}, allerede under oppfølging.")
            metricsService.registrer(Events.REGISTRERING_TILSTANDSFEIL, Tilstandsfeil.ALLEREDE_UNDER_OPPFOLGING)
            throw RuntimeException("Bruker kan ikke registreres ordinært fordi hen allerede er under oppfølging.")
        }
        if (brukersTilstand.ikkeErOrdinaerRegistrering()) {
            metricsService.registrer(Events.REGISTRERING_TILSTANDSFEIL, Tilstandsfeil.IKKE_ORDINAER_REGISTRERING)
            throw RuntimeException(
                    "Brukeren kan ikke registreres ordinært fordi utledet registreringstype er ${brukersTilstand.registreringstype}"
            )
        }
        try {
            validerBrukerRegistrering(ordinaerBrukerRegistrering)
        } catch (e: RuntimeException) {
            LOG.warn(
                "Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}",
                ordinaerBrukerRegistrering.besvarelse,
                ordinaerBrukerRegistrering.sisteStilling
            )
            metricsService.registrer(Events.INVALID_REGISTRERING_EVENT)
            throw e
        }
    }

    private fun lagreManuellRegistrering(brukerRegistrering: OrdinaerBrukerRegistrering, veileder: NavVeileder?) {
        if (veileder == null) return
        val manuellRegistrering = ManuellRegistrering(
            brukerRegistrering.id,
            brukerRegistrering.hentType(),
            veileder.veilederIdent,
            veileder.enhetsId
        )

        try {
            manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering)
        } catch (e: RuntimeException) {
            LOG.error("Lagring av veilederinfo feilet: ${manuellRegistrering.veilederEnhetId}")
            throw e
        }
    }

    private fun profilerBrukerTilInnsatsgruppe(fnr: Foedselsnummer, besvarelse: Besvarelse): Profilering {
        return profileringService.profilerBruker(
            fnr.alder(LocalDate.now()),
            fnr,
            besvarelse
        )
    }

    @Transactional(noRollbackFor = [AktiverBrukerException::class, AktiverBrukerTekniskException::class])
    open fun overforArena(registreringId: Long, bruker: Bruker, veileder: NavVeileder?) {
        val registreringTilstand = overforArena(registreringId, bruker)
        if (registreringTilstand.status !== Status.OVERFORT_ARENA) {
            throw AktiverBrukerException("Feil ved overføring til Arena: ${registreringTilstand.status}", fromStatus(registreringTilstand.status))
        }

        startArbeidssokerperiode(bruker)
        registrerOverfortStatistikk(veileder)
    }

    private fun overforArena(registreringId: Long, bruker: Bruker): RegistreringTilstand {
        val (innsatsgruppe) = profileringRepository.hentProfileringForId(registreringId)
        try {
            oppfolgingGateway.aktiverBruker(bruker.gjeldendeFoedselsnummer, innsatsgruppe)
        } catch (e: AktiverBrukerException) {
            val oppdatertRegistreringTilstand = oppdaterRegistreringTilstand(registreringId, from(e.aktiverBrukerFeil))
            LOG.info("Overføring av registrering (id: {}) til Arena feilet med {}", registreringId, e.aktiverBrukerFeil)
            return oppdatertRegistreringTilstand
        } catch (e: RuntimeException) {
            oppdaterRegistreringTilstand(registreringId, Status.UKJENT_TEKNISK_FEIL)
            throw AktiverBrukerTekniskException(e)
        }

        val oppdatertRegistreringTilstand = oppdaterRegistreringTilstand(registreringId, Status.OVERFORT_ARENA)
        LOG.info("Overføring av registrering (id: {}) til Arena gjennomført", registreringId)
        return oppdatertRegistreringTilstand
    }

    private fun startArbeidssokerperiode(bruker: Bruker) {
        try {
            arbeidssokerperiodeService.startPeriode(bruker)
        } catch (e: RuntimeException) {
            LOG.error("Feil ved starting av ny arbeidssøkerperiode", e)
        }
    }

    private fun oppdaterRegistreringTilstand(registreringId: Long, status: Status): RegistreringTilstand {
        val aktiveringTilstand = registreringTilstandRepository
            .hentTilstandFor(registreringId)
            .oppdaterStatus(status)
        return registreringTilstandRepository.oppdater(aktiveringTilstand)
    }

    private fun registrerOverfortStatistikk(veileder: NavVeileder?) {
        if (veileder == null) return
        metricsService.registrer(Events.MANUELL_REGISTRERING_EVENT, BrukerRegistreringType.ORDINAER)
    }

    fun registrerAtArenaHarPlanlagtNedetid() {
        metricsService.registrer(Events.REGISTRERING_NEDETID_ARENA)
    }

    fun hentNesteOpplysningerOmArbeidssoker(antall: Int): List<Pair<Long, OpplysningerOmArbeidssoekerMottatt>> =
        brukerRegistreringRepository.hentNesteOpplysningerOmArbeidssoeker(antall).map { (id, opplysninger) ->
            Triple(id, opplysninger, manuellRegistreringRepository.hentManuellRegistrering(id, BrukerRegistreringType.ORDINAER))
        }.map { (id, opplysninger, manuellRegistrering) ->
            id to (manuellRegistrering?.let {
                opplysninger.copy(
                    opplysningerOmArbeidssoeker = opplysninger.opplysningerOmArbeidssoeker.copy(
                        metadata = opplysninger.opplysningerOmArbeidssoeker.metadata.copy(
                            utfoertAv = no.nav.paw.arbeidssokerregisteret.intern.v1.vo.Bruker(
                                type = BrukerType.VEILEDER,
                                id = manuellRegistrering.veilederIdent
                            )
                        )
                    )
                )
            } ?: opplysninger)
        }

    fun settOpplysningerOmArbeidssoekerSomOverfort(listeMedIder: List<Int>) {
        brukerRegistreringRepository.settOpplysningerOmArbeidssoekerSomOverfort(listeMedIder)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(BrukerRegistreringService::class.java)
    }
}
