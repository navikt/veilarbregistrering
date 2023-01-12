package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface AktorIdCacheRepository {

    fun lagre(aktorIdCache: AktorIdCache)
    fun lagreBolk(aktorIdCacheListe: List<AktorIdCache>): Int
    fun hentAktørId(fnr: Foedselsnummer): AktorIdCache?
}