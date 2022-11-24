package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

class MeldekortService(
    private val meldekortRepository: MeldekortRepository
) {
    fun hentMeldekort(foedselsnummer: Foedselsnummer) =
        meldekortRepository.hent(foedselsnummer).sortedByDescending { it.nåværendePeriode.periodeFra }

    fun hentSisteMeldekort(foedselsnummer: Foedselsnummer): MeldekortEvent? =
        meldekortRepository.hent(foedselsnummer).maxByOrNull { it.eventOpprettet }

    fun sisteMeldekortErSendtInnSiste14Dager(meldekortEvent: MeldekortEvent): Boolean{
        return meldekortEvent.eventOpprettet.isAfter(LocalDateTime.now().minusDays(14))
    }
}
