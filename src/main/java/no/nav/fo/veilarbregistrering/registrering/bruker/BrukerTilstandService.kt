package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.bruker.Resending.kanResendes
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData

class BrukerTilstandService(
        private val oppfolgingGateway: OppfolgingGateway,
        private val sykemeldingService: SykemeldingService,
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

        return BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforloepMetaData, harIgangsattRegistreringSomKanGjenopptas)
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
}