package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId

interface SykmeldtRegistreringRepository {

    fun lagreSykmeldtBruker(bruker: SykmeldtRegistrering, aktorId: AktorId): Long
    fun hentSykmeldtregistreringForAktorId(aktorId: AktorId): SykmeldtRegistrering?
}
