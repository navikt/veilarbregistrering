package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.sykemelding.Maksdato
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData
import org.slf4j.LoggerFactory
import kotlin.jvm.JvmOverloads
import java.time.LocalDateTime

class BrukerTilstandService(
    private val oppfolgingGateway: OppfolgingGateway,
    private val sykemeldingService: SykemeldingService,
    private val unleashService: UnleashService,
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
        brukerRegistreringRepository.hentOrdinaerBrukerregistreringForAktorIdOgTilstand(
            bruker.aktorId,
            Status.DOD_UTVANDRET_ELLER_FORSVUNNET,
            Status.MANGLER_ARBEIDSTILLATELSE
        )?.opprettetDato?.isAfter(LocalDateTime.now().minusDays(30)) ?: false


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
            "Lik registreringstype? {} - når {}",
            (brukersTilstand.getRegistreringstype() == brukersTilstandUtenSperret.getRegistreringstype()),
            maksdato
        )
    }

    private fun maksdato(brukersTilstand: BrukersTilstand): Maksdato =
        brukersTilstand.maksDato?.let(Maksdato::of) ?: Maksdato.nullable()


    private fun brukAvMaksdato(): Boolean {
        return !unleashService.isEnabled("veilarbregistrering.maksdatoToggletAv")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(BrukerTilstandService::class.java)
    }
}