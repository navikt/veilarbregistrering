package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.secureLogger
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

class PopulerHistoriskePerioderScheduler(
    private val populerArbeidssokerperioderService: PopulerArbeidssokerperioderService,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService,
    private val leaderElectionClient: LeaderElectionClient,
    private val unleashClient: UnleashClient
) {

    //@Scheduled(initialDelay = 180000, fixedDelay = Long.MAX_VALUE)
    fun populerHistoriskePerioder() {

        if (!leaderElectionClient.isLeader) {
            logger.info("Starter ikke jobb for å populere perioder, pod er ikke leader")
            return
        }

        logger.info("Starter jobb for å populere arbeidssøkerperioder")
        val alleFnr = formidlingsgruppeRepository.hentDistinkteFnrForArbeidssokere()
        logger.info("Populerer perioder for ${alleFnr.size} distinkte fødselsnumre")

        alleFnr.forEach {
            if (!unleashClient.isEnabled("veilarbregistrering.populer-perioder")) {
                logger.info("Toggle for jobb for populering er skrudd av - avbryter jobben")
                return
            }
            val identer = try {
                 pdlOppslagGateway.hentIdenter(fnr = it, erSystemKontekst = true)
            } catch (e: Exception) {
                logger.warn("Fant ikke identer i PDL for person")
                secureLogger.warn("Fant ikke identer i PDL for person med fnr $it")
                return@forEach
            }

            val aktorId = try {
                identer.finnGjeldendeAktorId()
            } catch (e: Exception) {
                AktorId("1234")
            }
            val gjeldendeFnr = try {
                identer.finnGjeldendeFnr()
            } catch (e: Exception) {
                it
            }

            val arbeidssoker = populerArbeidssokerperioderService.hentArbeidssøker(
                Bruker(
                    gjeldendeFnr,
                    aktorId,
                    identer.finnHistoriskeFoedselsnummer()
                )
            )

            arbeidssoker.allePerioder()
                .filter { periode -> periode.fraDato.isBefore(LocalDateTime.of(2023, 3, 1, 13, 40, 12)) }
                .forEach { periode -> arbeidssokerperiodeService.lagrePeriode(gjeldendeFnr, periode.fraDato, periode.tilDato) }
        }
        logger.info("Fullført jobb populering av perioder")
    }
}