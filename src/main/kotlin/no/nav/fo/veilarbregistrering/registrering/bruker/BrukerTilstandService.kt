package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway

class BrukerTilstandService(private val oppfolgingGateway: OppfolgingGateway) {

    //TODO: Flytte mer av aggregeringen fra "oppfølgingGateway" hit

    fun hentBrukersTilstand(bruker: Bruker): BrukersTilstand {
        val oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(bruker.gjeldendeFoedselsnummer)
        return BrukersTilstand.create(oppfolgingsstatus)
    }
}