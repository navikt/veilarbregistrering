package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

class ArbeidssokerperiodeService(val repository: ArbeidssokerperiodeRepository) {

    fun startPeriode(foedselsnummer: Foedselsnummer) {
        if (harAktivePeriode(foedselsnummer)) {
            throw IllegalStateException("Bruker har allerede en aktiv periode")
        }

        repository.startPeriode(foedselsnummer, LocalDateTime.now())
    }

    private fun harAktivePeriode(foedselsnummer: Foedselsnummer): Boolean {
        val perioder = repository.hentPerioder(foedselsnummer)

        if (perioder.isEmpty()) {
            return false
        }

        return perioder.first().til == null
    }
}
