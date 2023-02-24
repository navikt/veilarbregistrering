package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AktorIdCacheRepository {

    fun lagre(aktorIdCache: AktorIdCache)
    fun hentAktørId(fnr: Foedselsnummer): AktorIdCache?

    fun hentTilfeldigFnr(antall: Int): List<AktorIdCache>
}