package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssoker
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.JaNei
import no.nav.fo.veilarbregistrering.metrics.Metric
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import org.springframework.stereotype.Service

@Service
class ArbeidssokerService(
    private val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    private val formidlingsgruppeGateway: FormidlingsgruppeGateway,
    private val unleashClient: UnleashClient,
    private val metricsService: MetricsService,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val brukerReaktiveringRepository: ReaktiveringRepository
) {

    fun hentArbeidssokerperioder(bruker: Bruker, forespurtPeriode: Periode?): Arbeidssokerperioder {
        val arbeidssokerperioderORDS =
            formidlingsgruppeGateway.finnArbeissokerperioder(bruker.gjeldendeFoedselsnummer, forespurtPeriode!!)
        val overlappendeHistoriskePerioderORDS = arbeidssokerperioderORDS.overlapperMed(forespurtPeriode)

        val skalSammenlignePerioderORDS = unleashClient.isEnabled("veilarbregistrering.stopSammenlignePerioderORDS")

        if (skalSammenlignePerioderORDS) {
            val brukerRegistrering = brukerRegistreringRepository.hentBrukerregistreringForFoedselsnummer(bruker.alleFoedselsnummer())
            val brukerReaktivering = brukerReaktiveringRepository.finnReaktiveringerForFoedselsnummer(bruker.alleFoedselsnummer())
            val formidlingsgruppe =
                formidlingsgruppeRepository.hentFormidlingsgrupperOgMapTilFormidlingsgruppeEndretEvent(bruker.alleFoedselsnummer())

            val arbeidssoker = Arbeidssoker()

            brukerRegistrering.forEach { arbeidssoker.behandle(it) }
            brukerReaktivering.forEach { arbeidssoker.behandle(it) }
            formidlingsgruppe.forEach { arbeidssoker.behandle(it) }

            // TODO: Må "spilles av" i riktig rekkefølge
            val overlappendeArbeidssokerperioderLokalt = Arbeidssokerperioder(
                arbeidssoker.allePerioder().map {
                    Arbeidssokerperiode(
                        Periode(
                            it.fraDato.toLocalDate(),
                            it.tilDato?.toLocalDate()
                        )
                    )
                }
            ).overlapperMed(forespurtPeriode)

            val lokalErLikORDS =
                overlappendeArbeidssokerperioderLokalt == overlappendeHistoriskePerioderORDS

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
