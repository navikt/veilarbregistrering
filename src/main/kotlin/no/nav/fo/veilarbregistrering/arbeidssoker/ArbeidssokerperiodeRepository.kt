package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

interface ArbeidssokerperiodeRepository {
    fun startPeriode(foedselsnummer: Foedselsnummer, fraDato: LocalDateTime)
    fun avsluttPeriode(foedselsnummer: Foedselsnummer, tilDato: LocalDateTime)
    fun hentPerioder(foedselsnummer: Foedselsnummer): List<ArbeidssokerperiodeDto>
}
