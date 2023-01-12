package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeRepository
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

class PopulerAktorIdWorker(
    val formidlingsgruppeRepository: FormidlingsgruppeRepository,
    val aktorIdCacheRepository: AktorIdCacheRepository
) {
    fun populereAktorId () {
        val foedselsnummer: List<Foedselsnummer> = formidlingsgruppeRepository.hentUnikeFoedselsnummer()



    }





}