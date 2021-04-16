package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.bruker.Resending.kanResendes
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.sykemelding.Maksdato
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData
import org.slf4j.LoggerFactory
import kotlin.jvm.JvmOverloads

class BrukerTilstandService(
        private val oppfolgingGateway: OppfolgingGateway,
        private val sykemeldingService: SykemeldingService,
        private val unleashClient: UnleashClient,
        private val brukerRegistreringRepository: BrukerRegistreringRepository,
) {
    @JvmOverloads
    fun hentBrukersTilstand(bruker: Bruker, sykmeldtRegistrering: Boolean = false): BrukersTilstand {
        val oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(bruker.gjeldendeFoedselsnummer)
        var sykeforloepMetaData: SykmeldtInfoData? = null
        val erSykmeldtMedArbeidsgiver = oppfolgingsstatus.erSykmeldtMedArbeidsgiver.orElse(false)
        if (erSykmeldtMedArbeidsgiver) {
            sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(bruker.gjeldendeFoedselsnummer)
        }

        val harIgangsattRegistreringSomKanGjenopptas = harIgangsattRegistreringSomKanGjenopptas(bruker)

        val brukersTilstand =
            BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData, harIgangsattRegistreringSomKanGjenopptas)

        if (brukAvMaksdato()) {
            LOG.info("Benytter maksdato")
            return brukersTilstand
        }
        val brukersTilstandUtenSperret = BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforloepMetaData, harIgangsattRegistreringSomKanGjenopptas)
        loggfoerRegistreringstype(sykmeldtRegistrering, brukersTilstand, brukersTilstandUtenSperret)
        return brukersTilstandUtenSperret
    }

    private fun harIgangsattRegistreringSomKanGjenopptas(bruker: Bruker): Boolean =
        kanResendes(
            brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(
                bruker.aktorId,
                listOf(
                    Status.DOD_UTVANDRET_ELLER_FORSVUNNET,
                    Status.MANGLER_ARBEIDSTILLATELSE,
                )
            ).firstOrNull()
        )


    private fun loggfoerRegistreringstype(
        sykmeldtRegistrering: Boolean,
        brukersTilstand: BrukersTilstand,
        brukersTilstandUtenSperret: BrukersTilstandUtenSperret
    ) {
        if (!sykmeldtRegistrering) {
            LOG.info("Benytter ikke maksdato")
            return
        }

        var maksdato = maksdato(brukersTilstand)
        LOG.info(
            "Lik registreringstype? {} - når {} - med / uten: {} / {}",
            (brukersTilstand.getRegistreringstype() == brukersTilstandUtenSperret.getRegistreringstype()),
            maksdato,
            brukersTilstand.getRegistreringstype(),
            brukersTilstandUtenSperret.getRegistreringstype()
        )
    }

    private fun maksdato(brukersTilstand: BrukersTilstand): Maksdato =
        brukersTilstand.maksDato?.let(Maksdato::of) ?: Maksdato.nullable()


    private fun brukAvMaksdato(): Boolean {
        return !unleashClient.isEnabled("veilarbregistrering.maksdatoToggletAv")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(BrukerTilstandService::class.java)
    }
}