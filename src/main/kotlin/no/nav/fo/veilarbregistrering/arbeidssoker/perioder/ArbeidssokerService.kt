package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.JaNei
import no.nav.fo.veilarbregistrering.metrics.Metric
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.stereotype.Service

@Service
class ArbeidssokerService(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val formidlingsgruppeGateway: FormidlingsgruppeGateway,
    private val unleashClient: UnleashClient,
    private val metricsService: MetricsService
) {

    fun hentArbeidssokerperioder(bruker: Bruker, forespurtPeriode: Periode?): Arbeidssokerperioder {
        val arbeidssokerperioderORDS =
            formidlingsgruppeGateway.finnArbeissokerperioder(bruker.gjeldendeFoedselsnummer, forespurtPeriode!!)
        val overlappendeHistoriskePerioderORDS = arbeidssokerperioderORDS.overlapperMed(forespurtPeriode)

        val arbeidssokerperioderLokalt =
            formidlingsgruppeRepository.finnFormidlingsgrupperOgMapTilArbeidssokerperioder(bruker.alleFoedselsnummer())
        val overlappendeArbeidssokerperioderLokalt = arbeidssokerperioderLokalt.overlapperMed(forespurtPeriode)

        val lokalErLikORDS = overlappendeArbeidssokerperioderLokalt == overlappendeHistoriskePerioderORDS

        metricsService.registrer(
            Events.HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR,
            if (lokalErLikORDS) JaNei.JA else JaNei.NEI
        )

        if (!lokalErLikORDS) {
            logger.warn(
                "Periodelister fra lokal cache og Arena-ORDS er ikke like\n" +
                    "Forespurt periode: $forespurtPeriode\n" +
                    "Lokalt:\n$overlappendeArbeidssokerperioderLokalt\n" +
                    "Arena-ORDS:\n$overlappendeHistoriskePerioderORDS"
            )
        }

        metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS)
        logger.info(
            "Returnerer arbeidssokerperioder fra Arena sin ORDS-tjenesten uten sammenligning: $overlappendeHistoriskePerioderORDS"
        )

        return overlappendeHistoriskePerioderORDS
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
        const val VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE = "veilarbregistrering.formidlingsgruppe.localcache"
    }
}
