package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status

interface BrukerRegistreringRepository {
    fun lagre(registrering: OrdinaerBrukerRegistrering, bruker: Bruker): OrdinaerBrukerRegistrering
    fun lagreSykmeldtBruker(bruker: SykmeldtRegistrering, aktorId: AktorId): Long
    fun hentBrukerregistreringForId(brukerregistreringId: Long): OrdinaerBrukerRegistrering
    fun hentOrdinaerBrukerregistreringForAktorIdOgTilstand(
        aktorId: AktorId,
        vararg tilstander: Status
    ): OrdinaerBrukerRegistrering?

    fun hentOrdinaerBrukerregistreringForAktorId(aktorId: AktorId): OrdinaerBrukerRegistrering?
    fun hentSykmeldtregistreringForAktorId(aktorId: AktorId): SykmeldtRegistrering?
    fun hentBrukerTilknyttet(brukerRegistreringId: Long): Bruker
}