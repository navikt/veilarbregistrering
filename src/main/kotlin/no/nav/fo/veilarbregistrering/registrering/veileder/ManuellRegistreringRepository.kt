package no.nav.fo.veilarbregistrering.registrering.veileder

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType

interface ManuellRegistreringRepository {
    fun lagreManuellRegistrering(manuellRegistrering: ManuellRegistrering): Long
    fun hentManuellRegistrering(
        registreringId: Long,
        brukerRegistreringType: BrukerRegistreringType
    ): ManuellRegistrering?
}