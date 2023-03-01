package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssoker
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeGateway
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
    private val formidlingsgruppeGateway: FormidlingsgruppeGateway,
    private val populerArbeidssokerperioderService: PopulerArbeidssokerperioderService,
    private val unleashClient: UnleashClient,
    private val metricsService: MetricsService,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
    private val brukerReaktiveringRepository: ReaktiveringRepository,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService
) {

    fun hentArbeidssokerperioder(bruker: Bruker, forespurtPeriode: Periode?): Arbeidssokerperioder {
        val arbeidssokerperioderORDS =
            formidlingsgruppeGateway.finnArbeissokerperioder(bruker.gjeldendeFoedselsnummer, forespurtPeriode!!)
        val overlappendeHistoriskePerioderORDS = arbeidssokerperioderORDS.overlapperMed(forespurtPeriode)

        val skalSammenlignePerioderORDS = unleashClient.isEnabled("veilarbregistrering.stopSammenlignePerioderORDS")

        val lagredePerioder = arbeidssokerperiodeService.hentPerioder(bruker.gjeldendeFoedselsnummer)
        val allePerioder = konkatinerLagredeOgHistoriskePerioder(lagredePerioder, overlappendeHistoriskePerioderORDS)

        if (skalSammenlignePerioderORDS) {
            try {
                val arbeidssoker = populerArbeidssokerperioderService.hentArbeidssøker(bruker)
                val overlappendeArbeidssokerperioderLokalt = map(arbeidssoker).overlapperMed(forespurtPeriode)
                sammenlignNyOgGammelModell(
                    overlappendeArbeidssokerperioderLokalt,
                    overlappendeHistoriskePerioderORDS,
                    forespurtPeriode
                )


            } catch (e: RuntimeException) {
                logger.warn("Sammenligning av perioder feilet", e)
            }
        }

        metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS)
        logger.info(
            "Returnerer arbeidssokerperioder fra Arena sin ORDS-tjenesten uten sammenligning: $overlappendeHistoriskePerioderORDS"
        )

        return allePerioder
    }

    private fun konkatinerLagredeOgHistoriskePerioder(lagredePerioder: List<Periode>, ordsPerioder: Arbeidssokerperioder): Arbeidssokerperioder {
        val lagredeArbeidssokerperioder = Arbeidssokerperioder.of(lagredePerioder.map { Arbeidssokerperiode(it) })
        val allePerioder = (ordsPerioder.asList() + lagredeArbeidssokerperioder.asList()).distinct()
        return Arbeidssokerperioder(allePerioder)
    }

    private fun map(arbeidssoker: Arbeidssoker) = Arbeidssokerperioder(
        arbeidssoker.allePerioder().map {
            Arbeidssokerperiode(Periode(it.fraDato.toLocalDate(), it.tilDato?.toLocalDate()))
        }
    )

    private fun sammenlignNyOgGammelModell(
        overlappendeArbeidssokerperioderLokalt: Arbeidssokerperioder,
        overlappendeHistoriskePerioderORDS: Arbeidssokerperioder,
        forespurtPeriode: Periode
    ) {
        if (overlappendeArbeidssokerperioderLokalt == overlappendeHistoriskePerioderORDS) {
            logger.info("Periodelister fra lokal cache og Arena-ORDS er like. Forespurt periode: $forespurtPeriode")
            metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR, JaNei.JA)
        } else {
            logger.warn(
                "Periodelister fra lokal cache og Arena-ORDS er ikke like\n" +
                        "Forespurt periode: $forespurtPeriode\n" +
                        "Lokalt:\n$overlappendeArbeidssokerperioderLokalt\n" +
                        "Arena-ORDS:\n$overlappendeHistoriskePerioderORDS"
            )

            metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR, JaNei.NEI)
        }
    }

    private enum class Kilde : Metric {
        ORDS, LOKAL;

        override fun fieldName(): String = "kilde"
        override fun value(): Any = this.toString()
    }

    companion object {
        const val VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE = "veilarbregistrering.formidlingsgruppe.localcache"
    }
}
