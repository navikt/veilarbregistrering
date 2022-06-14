package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.JaNei
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.BrukerRegistreringWrapper
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.BrukerRegistreringWrapperFactory
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.formidling.Status.*
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.ordinaer.kanResendes
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringRepository

class HentRegistreringService(
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val sykmeldtRegistreringRepository: SykmeldtRegistreringRepository,
    private val profileringRepository: ProfileringRepository,
    private val manuellRegistreringRepository: ManuellRegistreringRepository,
    private val norg2Gateway: Norg2Gateway,
    private val metricsService: MetricsService
) {
    fun hentBrukerregistrering(bruker: Bruker): BrukerRegistreringWrapper? {
        val ordinaerBrukerRegistrering = hentOrdinaerBrukerRegistrering(bruker)
        val sykmeldtBrukerRegistrering = hentSykmeldtRegistrering(bruker)
        val brukerRegistreringWrapper =
            BrukerRegistreringWrapperFactory.create(ordinaerBrukerRegistrering, sykmeldtBrukerRegistrering)
        if (brukerRegistreringWrapper == null) {
            logger.info("Bruker ble ikke funnet i databasen.")
            metricsService.registrer(Events.HENT_BRUKERREGISTRERING_BRUKER_FUNNET, JaNei.NEI)
        }
        metricsService.registrer(Events.HENT_BRUKERREGISTRERING_BRUKER_FUNNET, JaNei.JA)
        return brukerRegistreringWrapper
    }

    fun hentOrdinaerBrukerRegistrering(bruker: Bruker): OrdinaerBrukerRegistrering? = hentOrdinaerBrukerRegistrering(
        bruker,
        listOf(OVERFORT_ARENA, PUBLISERT_KAFKA, OPPRINNELIG_OPPRETTET_UTEN_TILSTAND)
    )

    fun hentIgangsattOrdinaerBrukerRegistrering(bruker: Bruker): OrdinaerBrukerRegistrering? {
        val ordinaerBrukerRegistrering = hentOrdinaerBrukerRegistrering(
            bruker,
            listOf(DOD_UTVANDRET_ELLER_FORSVUNNET, MANGLER_ARBEIDSTILLATELSE)
        )
        return if (kanResendes(ordinaerBrukerRegistrering)) ordinaerBrukerRegistrering else null
    }

    private fun hentOrdinaerBrukerRegistrering(bruker: Bruker, status: List<Status>): OrdinaerBrukerRegistrering? {
        val ordinaerBrukerRegistrering = brukerRegistreringRepository
            .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(bruker.aktorId, status)
            .firstOrNull()
            ?: return null

        val veileder = hentManuellRegistreringVeileder(
            ordinaerBrukerRegistrering.id, ordinaerBrukerRegistrering.hentType()
        )
        ordinaerBrukerRegistrering.manueltRegistrertAv = veileder
        val profilering = profileringRepository.hentProfileringForId(ordinaerBrukerRegistrering.id)
        return ordinaerBrukerRegistrering.med(profilering)
    }

    fun hentSykmeldtRegistrering(bruker: Bruker): SykmeldtRegistrering? {
        val sykmeldtBrukerRegistrering = sykmeldtRegistreringRepository
            .hentSykmeldtregistreringForAktorId(bruker.aktorId) ?: return null
        val veileder = hentManuellRegistreringVeileder(
            sykmeldtBrukerRegistrering.id, sykmeldtBrukerRegistrering.hentType()
        )
        sykmeldtBrukerRegistrering.manueltRegistrertAv = veileder
        return sykmeldtBrukerRegistrering
    }

    private fun hentManuellRegistreringVeileder(
        registreringId: Long,
        brukerRegistreringType: BrukerRegistreringType
    ): Veileder? {
        val (_, _, _, veilederIdent, veilederEnhetId) = manuellRegistreringRepository
            .hentManuellRegistrering(registreringId, brukerRegistreringType) ?: return null
        val enhet = finnEnhet(Enhetnr(veilederEnhetId))

        if (veilederEnhetId == null) {
            logger.warn("veilederEnhetId er null for registreringId: $registreringId")
        }

        logger.info("Henter NAV-enhet: ${enhet} for enhetId: $veilederEnhetId")

        return Veileder(veilederIdent, enhet)
    }

    fun finnEnhet(enhetId: Enhetnr): NavEnhet? {
        return try {
            norg2Gateway.hentAlleEnheter()[enhetId]
        } catch (e: Exception) {
            logger.error("Får ikke populert registrering med NAV-enhet pga. feil mot NORG2 - fortsetter uten.", e)
            null
        }
    }
}