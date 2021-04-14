package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.formidling.Status

interface BrukerRegistreringRepository {
    fun lagre(registrering: OrdinaerBrukerRegistrering, bruker: Bruker): OrdinaerBrukerRegistrering
    fun hentBrukerregistreringForId(brukerregistreringId: Long): OrdinaerBrukerRegistrering
    fun finnOrdinaerBrukerregistreringerFor(aktorId: AktorId): List<OrdinaerBrukerRegistrering>
    fun finnOrdinaerBrukerregistreringForAktorIdOgTilstand(aktorId: AktorId, tilstander: List<Status>): List<OrdinaerBrukerRegistrering>
    fun hentOrdinaerBrukerregistreringForAktorId(aktorId: AktorId): OrdinaerBrukerRegistrering?
    fun hentBrukerTilknyttet(brukerRegistreringId: Long): Bruker
}