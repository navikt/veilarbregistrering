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
            try {
                val arbeidssoker = populerNyArbeidssøkermodell(bruker)
                val overlappendeArbeidssokerperioderLokalt = map(arbeidssoker).overlapperMed(forespurtPeriode)
                sammenlignNyOgGammelModell(overlappendeArbeidssokerperioderLokalt, overlappendeHistoriskePerioderORDS, forespurtPeriode)
            } catch (e: RuntimeException) {
                logger.warn("Sammenligning av perioder feilet", e)
            }
        }

        metricsService.registrer(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS)
        logger.info(
            "Returnerer arbeidssokerperioder fra Arena sin ORDS-tjenesten uten sammenligning: $overlappendeHistoriskePerioderORDS"
        )

        return overlappendeHistoriskePerioderORDS
    }

    private fun populerNyArbeidssøkermodell(bruker: Bruker): Arbeidssoker {
        val brukerRegistrering =
            brukerRegistreringRepository.hentBrukerregistreringForFoedselsnummer(bruker.alleFoedselsnummer())
        val brukerReaktivering =
            brukerReaktiveringRepository.finnReaktiveringerForFoedselsnummer(bruker.alleFoedselsnummer())
        val formidlingsgruppe =
            formidlingsgruppeRepository.hentFormidlingsgrupperOgMapTilFormidlingsgruppeEndretEvent(bruker.alleFoedselsnummer())
        //TODO: hente meldekort - vi avventer Meldekort til vi har lært litt mer om modellen.

        val listeMedArbeidssøkerEndringer = brukerRegistrering + brukerReaktivering + formidlingsgruppe

        val arbeidssoker = Arbeidssoker()
        listeMedArbeidssøkerEndringer.sortedBy { it.opprettetTidspunkt() }.forEach { arbeidssoker.behandle(it) }

        return arbeidssoker
    }

    private fun map(arbeidssoker: Arbeidssoker) = Arbeidssokerperioder(
        arbeidssoker.allePerioder().map {
            Arbeidssokerperiode(
                Periode(
                    it.fraDato.toLocalDate(),
                    it.tilDato?.toLocalDate()
                )
            )
        }
    )

    private fun sammenlignNyOgGammelModell(
        overlappendeArbeidssokerperioderLokalt: Arbeidssokerperioder,
        overlappendeHistoriskePerioderORDS: Arbeidssokerperioder,
        forespurtPeriode: Periode
    ) {
        if (overlappendeArbeidssokerperioderLokalt == overlappendeHistoriskePerioderORDS) {
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
