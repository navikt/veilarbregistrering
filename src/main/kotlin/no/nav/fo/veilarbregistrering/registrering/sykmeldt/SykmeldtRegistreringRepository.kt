package no.nav.fo.veilarbregistrering.registrering.sykmeldt

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface SykmeldtRegistreringRepository {
    fun lagreSykmeldtBruker(sykmeldtRegistrering: SykmeldtRegistrering, bruker: Bruker): Long
    fun hentSykmeldtregistreringForAktorId(aktorId: AktorId): SykmeldtRegistrering?
    fun finnSykmeldtRegistreringerFor(aktorId: AktorId): List<SykmeldtRegistrering>

    fun finnAktorIdTilSykmeldtRegistreringUtenFoedselsnummer(maksAntall: Int, aktorIdDenyList: List<AktorId> = emptyList()): List<AktorId>
    fun oppdaterSykmeldtRegistreringerMedManglendeFoedselsnummer(aktorIdFoedselsnummerMap: Map<AktorId, Foedselsnummer>): IntArray
}
