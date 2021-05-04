package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.bruker.Resending.kanResendes
import no.nav.fo.veilarbregistrering.registrering.formidling.Status

class BrukerTilstandService(
        private val oppfolgingGateway: OppfolgingGateway,
        private val brukerRegistreringRepository: BrukerRegistreringRepository) {

    @JvmOverloads
    fun hentBrukersTilstand(bruker: Bruker, sykmeldtRegistrering: Boolean = false): BrukersTilstand {
        val oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(bruker.gjeldendeFoedselsnummer)
        val harIgangsattRegistreringSomKanGjenopptas = harIgangsattRegistreringSomKanGjenopptas(bruker)

        return BrukersTilstand(oppfolgingsstatus, harIgangsattRegistreringSomKanGjenopptas)
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