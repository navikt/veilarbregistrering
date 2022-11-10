package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface MeldekortRepository {
    fun lagre(meldekort: MeldekortEvent): Long
    fun hent(foedselsnummer: Foedselsnummer): List<MeldekortEvent>
}
