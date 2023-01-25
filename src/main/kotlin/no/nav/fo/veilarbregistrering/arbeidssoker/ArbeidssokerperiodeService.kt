package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

class ArbeidssokerperiodeService(val repository: ArbeidssokerperiodeRepository) {

    fun startPeriode(foedselsnummer: Foedselsnummer) {
        if (harAktivPeriode(foedselsnummer)) {
            throw IllegalStateException("Bruker har allerede en aktiv periode")
        }

        repository.startPeriode(foedselsnummer, LocalDateTime.now())
    }

    fun behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        if (formidlingsgruppeEndretEvent.erArbeidssoker()) {
            return
        }

        if (!harAktivPeriode(formidlingsgruppeEndretEvent.foedselsnummer)) {
            return
        }

        repository.avsluttPeriode(foedselsnummer = formidlingsgruppeEndretEvent.foedselsnummer, LocalDateTime.now())
    }

    private fun harAktivPeriode(foedselsnummer: Foedselsnummer): Boolean {
        return harAktivPeriode(repository.hentPerioder(foedselsnummer))
    }

    private fun harAktivPeriode(perioder: List<ArbeidssokerperiodeDto>): Boolean {
        if (perioder.isEmpty()) {
            return false
        }

        return perioder.first().til == null
    }
}
