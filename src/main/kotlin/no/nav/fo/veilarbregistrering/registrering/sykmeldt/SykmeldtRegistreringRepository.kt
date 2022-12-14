package no.nav.fo.veilarbregistrering.registrering.sykmeldt

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker

interface SykmeldtRegistreringRepository {
    fun lagreSykmeldtBruker(sykmeldtRegistrering: SykmeldtRegistrering, bruker: Bruker): Long
    fun hentSykmeldtregistreringForAktorId(aktorId: AktorId): SykmeldtRegistrering?
    fun finnSykmeldtRegistreringerFor(aktorId: AktorId): List<SykmeldtRegistrering>
}
