package no.nav.fo.veilarbregistrering.profilering

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status

class ProfilertInnsatsgruppeService(
    private val oppfolgingGateway: OppfolgingGateway,
    private val profileringRepository: ProfileringRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
) {

    fun hentProfilering(bruker: Bruker): Pair<Innsatsgruppe?, Servicegruppe?> {
        val oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(bruker.gjeldendeFoedselsnummer)
        val brukerregistrering = brukerRegistreringRepository
            .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(bruker.aktorId, listOf(
                Status.OVERFORT_ARENA,
                Status.PUBLISERT_KAFKA,
                Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND
            ))
            .firstOrNull()

        val profilering = brukerregistrering?.let { profileringRepository.hentProfileringForId(brukerregistrering.id)  }

        return Pair(profilering?.innsatsgruppe, oppfolgingsstatus.servicegruppe)
    }

    fun erStandardInnsats(bruker: Bruker): Boolean {
        val (innsatsgruppe, servicegruppe) = hentProfilering(bruker)

        return if (servicegruppe?.value() === "IVURD") {
            innsatsgruppe === Innsatsgruppe.STANDARD_INNSATS
        } else {
            servicegruppe?.value() === "IKVAL"
        }
    }
}
