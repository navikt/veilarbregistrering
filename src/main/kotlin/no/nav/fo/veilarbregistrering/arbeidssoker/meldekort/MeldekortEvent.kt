package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.arbeidssoker.MeldekortEndret
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

data class MeldekortEvent(
    val fnr: Foedselsnummer,
    val erArbeidssokerNestePeriode: Boolean,
    val nåværendePeriode: MeldekortPeriode,
    val meldekorttype: Meldekorttype,
    val meldekortEventId: Long,
    val eventOpprettet: LocalDateTime
): MeldekortEndret {
    fun erSendtInnSiste14Dager(): Boolean = eventOpprettet.isAfter(LocalDateTime.now().minusDays(14))
    override fun erArbeidssokerNestePeriode(): Boolean = erArbeidssokerNestePeriode
    override fun opprettetTidspunkt(): LocalDateTime = eventOpprettet
}

enum class Meldekorttype {
    ORDINAER,
    ERSTATNING,
    RETUR,
    ELEKTRONISK,
    AAP,
    ORDINAER_MANUELL,
    MASKINELT_OPPDATERT,
    MANUELL_ARENA,
    KORRIGERT_ELEKTRONISK;

    companion object {
        fun from(meldekorttype: String): Meldekorttype =
            values().find { it.name == meldekorttype }
                ?: throw RuntimeException("Fant ikke meldekorttype for: $meldekorttype")
    }
}
