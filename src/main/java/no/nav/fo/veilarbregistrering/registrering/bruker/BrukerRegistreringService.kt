package no.nav.fo.veilarbregistrering.registrering.bruker

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil.Companion.fromStatus
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse
import no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand.Companion.medStatus
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.formidling.Status.Companion.from
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

open class BrukerRegistreringService(
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val profileringRepository: ProfileringRepository,
    private val oppfolgingGateway: OppfolgingGateway,
    private val profileringService: ProfileringService,
    private val registreringTilstandRepository: RegistreringTilstandRepository,
    private val brukerTilstandService: BrukerTilstandService,
    private val manuellRegistreringRepository: ManuellRegistreringRepository,
    private val prometheusMetricsService: PrometheusMetricsService
) {
    @Transactional
    open fun registrerBrukerUtenOverforing(
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
        prometheusMetricsService.registrer(
            Events.PROFILERING_EVENT,
            Tag.of("innsatsgruppe", profilering.innsatsgruppe.arenakode)
        )
        rapporterOrdinaerBesvarelse(prometheusMetricsService, ordinaerBrukerRegistrering, profilering)
        val registreringTilstand = medStatus(Status.MOTTATT, opprettetBrukerRegistrering.id)
        registreringTilstandRepository.lagre(registreringTilstand)
        LOG.info(
            "Brukerregistrering (id: {}) gjennomført med data {}, Profilering {}",
            opprettetBrukerRegistrering.id,
            opprettetBrukerRegistrering,
            profilering
        )
        return opprettetBrukerRegistrering
    }

    private fun lagreManuellRegistrering(brukerRegistrering: OrdinaerBrukerRegistrering, veileder: NavVeileder?) {
        if (veileder == null) return
        val manuellRegistrering = ManuellRegistrering(
            brukerRegistrering.id,
            brukerRegistrering.hentType(),
            veileder.veilederIdent,
            veileder.enhetsId
        )
        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering)
    }

    @Transactional(noRollbackFor = [AktiverBrukerException::class])
    open fun overforArena(registreringId: Long, bruker: Bruker, veileder: NavVeileder?) {
        val registreringTilstand = overforArena(registreringId, bruker)
        if (registreringTilstand.status !== Status.OVERFORT_ARENA) {
            throw AktiverBrukerException(fromStatus(registreringTilstand.status))
        }
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
        }
        val oppdatertRegistreringTilstand = oppdaterRegistreringTilstand(registreringId, Status.OVERFORT_ARENA)
        LOG.info("Overføring av registrering (id: {}) til Arena gjennomført", registreringId)
        return oppdatertRegistreringTilstand
    }

    private fun oppdaterRegistreringTilstand(registreringId: Long, status: Status): RegistreringTilstand {
        val aktiveringTilstand = registreringTilstandRepository
            .hentTilstandFor(registreringId)
            .oppdaterStatus(status)
        return registreringTilstandRepository.oppdater(aktiveringTilstand)
    }

    private fun registrerOverfortStatistikk(veileder: NavVeileder?) {
        if (veileder == null) return
        prometheusMetricsService.registrer(Events.MANUELL_REGISTRERING_EVENT, BrukerRegistreringType.ORDINAER)
    }

    private fun validerBrukerRegistrering(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering, bruker: Bruker) {
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker)
        if (brukersTilstand.isUnderOppfolging) {
            throw RuntimeException("Bruker allerede under oppfølging.")
        }
        if (brukersTilstand.ikkeErOrdinaerRegistrering()) {
            throw RuntimeException(
                String.format(
                    "Brukeren kan ikke registreres ordinært fordi utledet registreringstype er %s.",
                    brukersTilstand.registreringstype
                )
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
            prometheusMetricsService.registrer(Events.INVALID_REGISTRERING_EVENT)
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

    companion object {
        private val LOG = LoggerFactory.getLogger(BrukerRegistreringService::class.java)
    }
}