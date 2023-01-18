package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Gruppe
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import java.time.LocalDateTime

class AktorIdCacheService(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val aktorIdCacheRepository: AktorIdCacheRepository,
) {
    fun sjekkFoedselsnummerOgHvisNeiSettInn(foedselsnummer: Foedselsnummer) {
        if (aktorIdCacheRepository.hentAktørId(foedselsnummer) != null) return
        val identer = pdlOppslagGateway.hentIdenter(foedselsnummer)

        // hente ut
        val aktorId = identer.identer.first { !it.isHistorisk && it.gruppe == Gruppe.AKTORID }.ident
        // sette inn

        val aktorIdCache = AktorIdCache(
            foedselsnummer = foedselsnummer,
            aktorId = AktorId(aktorId),
            opprettetDato = LocalDateTime.now()
        )

        aktorIdCacheRepository.lagre(aktorIdCache)
    }


}