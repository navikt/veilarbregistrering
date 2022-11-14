package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

class MeldekortService(
    private val meldekortRepository: MeldekortRepository
) {
    fun hentMeldekort(foedselsnummer: Foedselsnummer) = meldekortRepository.hent(foedselsnummer)
}
