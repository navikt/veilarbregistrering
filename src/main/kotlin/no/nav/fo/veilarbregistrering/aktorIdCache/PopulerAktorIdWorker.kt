package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.log.logger
import java.time.LocalDateTime

class PopulerAktorIdWorker(
    val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    val pdlOppslagGateway: PdlOppslagGateway,
    val aktorIdCacheRepository: AktorIdCacheRepository
) {
    fun populereAktorId() {
        val foedselsnummer: List<Foedselsnummer> = formidlingsgruppeRepository.hentUnikeFoedselsnummer()

        val aktorIdFnrMap = pdlOppslagGateway.hentIdenterBolk(foedselsnummer)

        val oppdaterteRader =
            aktorIdCacheRepository.lagreBolk(aktorIdFnrMap.map { AktorIdCache(it.key, it.value, LocalDateTime.now()) })

        logger.info("Oppdaterte $oppdaterteRader i jobb som populerer AktørId-cache")
    }

}