package no.nav.fo.veilarbregistrering.profilering

interface ProfileringRepository {
    fun lagreProfilering(brukerregistreringId: Long, profilering: Profilering)
    fun hentProfileringForId(brukerregistreringId: Long): Profilering
}