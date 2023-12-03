package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

interface ArbeidssokerperiodeRepository {
    fun startPeriode(foedselsnummer: Foedselsnummer, fraDato: LocalDateTime)
    fun avsluttPeriode(foedselsnummer: Foedselsnummer, tilDato: LocalDateTime)
    fun avsluttPeriode(id: Int, tilDato: LocalDateTime)
    fun hentPerioder(foedselsnummer: Foedselsnummer): List<ArbeidssokerperiodeDto>
    fun hentPerioder(gjeldendeFoedselsnummer: Foedselsnummer, historiskeFoedselsnummer: List<Foedselsnummer>): List<ArbeidssokerperiodeDto>
    fun hentNesteArbeidssokerperioder(antall: Int): List<ArbeidssokerperiodeDto>
    fun settArbeidssokerperioderSomOverfort(listeMedIder: List<Int>)
}
