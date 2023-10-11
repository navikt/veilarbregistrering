package no.nav.fo.veilarbregistrering.profilering

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status

class ProfilertInnsatsgruppeService(
    private val oppfolgingGateway: OppfolgingGateway,
    private val profileringRepository: ProfileringRepository,
    private val brukerRegistreringRepository: BrukerRegistreringRepository,
) {

    fun erStandardInnsats(bruker: Bruker): Boolean {
        val (innsatsgruppe, servicegruppe, formidlingsgruppe) = hentProfilering(bruker)
        val brukInnsatsgruppe = servicegruppe?.value() == "IVURD" || (servicegruppe == null && isDevelopment());

        return if (brukInnsatsgruppe) {
            innsatsgruppe == Innsatsgruppe.STANDARD_INNSATS
        } else {
            servicegruppe?.value() == "IKVAL" && formidlingsgruppe?.erArbeidssoker() ?: false;
        }
    }

    fun hentProfilering(bruker: Bruker): Triple<Innsatsgruppe?, Servicegruppe?, Formidlingsgruppe?> {
        val arenaStatus = oppfolgingGateway.arenaStatus(bruker.gjeldendeFoedselsnummer)
        val brukerregistrering = brukerRegistreringRepository
            .finnOrdinaerBrukerregistreringForAktorIdOgTilstand(bruker.aktorId, listOf(
                Status.OVERFORT_ARENA,
                Status.PUBLISERT_KAFKA,
                Status.OPPRINNELIG_OPPRETTET_UTEN_TILSTAND
            ))
            .firstOrNull()

        val profilering = brukerregistrering?.let { profileringRepository.hentProfileringForId(brukerregistrering.id)  }

        return Triple(profilering?.innsatsgruppe, arenaStatus?.servicegruppe, arenaStatus?.formidlingsgruppe)
    }
}
