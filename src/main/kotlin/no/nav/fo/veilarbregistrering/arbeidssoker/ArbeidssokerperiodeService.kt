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
        if (formidlingsgruppeEndretEvent.erArbeidssoker()) {
            return
        }

        val bruker = userService.finnBrukerGjennomPdlForSystemkontekst(formidlingsgruppeEndretEvent.foedselsnummer)
        val aktivPeriode = hentAktivPeriode(bruker) ?: return

        if (aktivPeriode.foedselsnummer != bruker.gjeldendeFoedselsnummer) {
            // TODO skal vi i dette tilfellet oppdatere fødselsnummeret på perioden?
            logger.warn("Avslutter periode for person som har et annet gjeldende fødselsnummer enn den aktive perioden")
        }

        repository.avsluttPeriode(id = aktivPeriode.id, LocalDateTime.now())
    }

    fun hentPerioder(bruker: Bruker): List<Periode> {
        return repository.hentPerioder(bruker.gjeldendeFoedselsnummer, bruker.historiskeFoedselsnummer)
            .map { Periode(it.fra.toLocalDate(), it.til?.toLocalDate()) }
    }

    fun hentNesteArbeidssokerperioder(): List<ArbeidssokerperiodeDto> {
        return repository.hentNesteArbeidssokerperioder()
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
