package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.JaNei
import no.nav.fo.veilarbregistrering.metrics.Metric
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArbeidssokerService(
    private val arbeidssokerRepository: ArbeidssokerRepository,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService,
    private val formidlingsgruppeGateway: FormidlingsgruppeGateway,
    private val unleashClient: UnleashClient,
    private val metricsService: MetricsService
) {
    @Transactional
    fun behandle(endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand) {

        if (isDevelopment()) {LOG.info("Formidlingsgruppe ble endret til ${endretFormidlingsgruppeCommand.formidlingsgruppe.kode}")}

        if (endretFormidlingsgruppeCommand.foedselsnummer == null) {
            LOG.warn(
                String.format(
                    "Foedselsnummer mangler for EndretFormidlingsgruppeCommand med person_id = %s",
                    endretFormidlingsgruppeCommand.personId
                )
            )
            return
        }
        if (endretFormidlingsgruppeCommand.formidlingsgruppeEndret.isBefore(LocalDateTime.parse("2010-01-01T00:00:00"))) {
            LOG.warn(
                String.format(
                    "Foreldet formidlingsgruppe-endring (%s) lest fra topic: 'gg-arena-formidlinggruppe-v1'  - denne forkastes.",
                    endretFormidlingsgruppeCommand.formidlingsgruppeEndret
                )
            )
            return
        }

        val eksisterendeArbeidssokerperioderLokalt = arbeidssokerRepository.finnFormidlingsgrupper(
            listOf(endretFormidlingsgruppeCommand.foedselsnummer!!)
        )

        arbeidssokerRepository.lagre(endretFormidlingsgruppeCommand)

        arbeidssokerperiodeService.behandleAvslutningAvArbeidssokerperiode(
            endretFormidlingsgruppeCommand,
            eksisterendeArbeidssokerperioderLokalt
        )

    }

    fun hentArbeidssokerperioder(bruker: Bruker, forespurtPeriode: Periode?): Arbeidssokerperioder {
        val arbeidssokerperioderLokalt = arbeidssokerRepository.finnFormidlingsgrupper(bruker.alleFoedselsnummer())
        LOG.info(String.format("Fant følgende arbeidssokerperioder i egen database: %s", arbeidssokerperioderLokalt))
        val arbeidssokerperioderORDS =
            formidlingsgruppeGateway.finnArbeissokerperioder(bruker.gjeldendeFoedselsnummer, forespurtPeriode!!)
        LOG.info(
            String.format(
                "Fikk følgende arbeidssokerperioder fra Arena sin ORDS-tjeneste: %s",
                arbeidssokerperioderORDS
            )
        )
        val dekkerHele = arbeidssokerperioderLokalt.dekkerHele(forespurtPeriode)
        val overlappendeArbeidssokerperioderLokalt = arbeidssokerperioderLokalt.overlapperMed(forespurtPeriode)
        val overlappendeHistoriskePerioderORDS = arbeidssokerperioderORDS.overlapperMed(forespurtPeriode)
        val lokalErLikOrds = overlappendeArbeidssokerperioderLokalt.equals(overlappendeHistoriskePerioderORDS)
        metricsService.registrer(
            Events.HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR,
            if (lokalErLikOrds) JaNei.JA else JaNei.NEI
        )
        if (!lokalErLikOrds) {
            LOG.warn(
                String.format(
                    "Periodelister fra lokal cache og Arena-ORDS er ikke like\nForespurt periode: %s\nLokalt: %s\nArena-ORDS: %s",
                    forespurtPeriode, overlappendeArbeidssokerperioderLokalt, overlappendeHistoriskePerioderORDS
                )
            )
        }
        if (dekkerHele && brukLokalCache()) {
            metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.LOKAL)
            LOG.info(
                String.format(
                    "Arbeidssokerperiodene fra egen database dekker hele perioden, og returneres: %s",
                    overlappendeArbeidssokerperioderLokalt
                )
            )
            return overlappendeArbeidssokerperioderLokalt
        }
        metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS)
        LOG.info(
            String.format(
                "Returnerer arbeidssokerperioder fra Arena sin ORDS-tjenesten: %s",
                overlappendeHistoriskePerioderORDS
            )
        )
        return overlappendeHistoriskePerioderORDS
    }

    private fun brukLokalCache(): Boolean {
        return unleashClient.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE)
    }

    private enum class Kilde : Metric {
        ORDS, LOKAL;

        override fun fieldName(): String {
            return "kilde"
        }

        override fun value(): Any {
            return this.toString()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidssokerService::class.java)
        const val VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE = "veilarbregistrering.formidlingsgruppe.localcache"
    }
}