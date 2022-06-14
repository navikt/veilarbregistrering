package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.kanResendes

class BrukerTilstandService(
    private val oppfolgingGateway: OppfolgingGateway,
    private val brukerRegistreringRepository: BrukerRegistreringRepository
) {
    fun hentBrukersTilstand(bruker: Bruker): BrukersTilstand {
        val oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(bruker.gjeldendeFoedselsnummer)
        val harIgangsattRegistreringSomKanGjenopptas = harIgangsattRegistreringSomKanGjenopptas(bruker)

        return BrukersTilstand.create(oppfolgingsstatus, harIgangsattRegistreringSomKanGjenopptas)
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