package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.logger
import java.time.LocalDateTime

class ArbeidssokerperiodeService(
    private val repository: ArbeidssokerperiodeRepository,
    private val userService: UserService
) {

    fun startPeriode(bruker: Bruker) {
        if (harAktivPeriode(bruker)) {
            throw IllegalStateException("Bruker har allerede en aktiv periode")
        }

        repository.startPeriode(bruker.gjeldendeFoedselsnummer, LocalDateTime.now())
    }

    fun behandleFormidlingsgruppeEvent(formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent) {
        val bruker = userService.finnBrukerGjennomPdlForSystemkontekst(formidlingsgruppeEndretEvent.foedselsnummer)
        val aktivPeriode = hentAktivPeriode(bruker)

        // Hvis formidlingsgruppeevent == "ARBS" og bruker har inaktiv periode, så starter vi perioden
        if (formidlingsgruppeEndretEvent.erArbeidssoker() && aktivPeriode == null) {
            repository.startPeriode(bruker.gjeldendeFoedselsnummer, LocalDateTime.now())
            return
        }
        // Hvis formidlingsgruppeevent != "ARBS" og bruker har aktiv periode, så avslutter vi perioden
        if (aktivPeriode != null && !formidlingsgruppeEndretEvent.erArbeidssoker()) {
            repository.avsluttPeriode(id = aktivPeriode.id, LocalDateTime.now())
        }
    }

    fun hentPerioder(bruker: Bruker): List<Periode> {
        return repository.hentPerioder(bruker.gjeldendeFoedselsnummer, bruker.historiskeFoedselsnummer)
            .map { Periode(it.fra.toLocalDate(), it.til?.toLocalDate()) }
    }

    fun hentNesteArbeidssokerperioder(antall: Int): List<ArbeidssokerperiodeDto> {
        return repository.hentNesteArbeidssokerperioder(antall)
    }

    fun settArbeidssokerperioderSomOverfort(listeMedIder: List<Int>) {
        repository.settArbeidssokerperioderSomOverfort(listeMedIder)
    }

    private fun harAktivPeriode(bruker: Bruker): Boolean {
        return hentAktivPeriode(bruker) != null
    }

    private fun hentAktivPeriode(bruker: Bruker): ArbeidssokerperiodeDto? {
        val perioder = repository.hentPerioder(bruker.gjeldendeFoedselsnummer, bruker.historiskeFoedselsnummer)

        if (perioder.isEmpty()) return null

        return perioder.find { it.til == null }
    }
}
