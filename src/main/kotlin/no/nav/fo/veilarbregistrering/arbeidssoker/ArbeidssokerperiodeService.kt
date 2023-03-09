package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.logger
import java.time.LocalDateTime

class ArbeidssokerperiodeService(
    private val repository: ArbeidssokerperiodeRepository,
    private val userService: UserService
) {

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

    private fun harAktivPeriode(foedselsnummer: Foedselsnummer): Boolean {
        return harAktivPeriode(repository.hentPerioder(foedselsnummer))
    }

    private fun harAktivPeriode(perioder: List<ArbeidssokerperiodeDto>): Boolean {
        if (perioder.isEmpty()) {
            return false
        }

        return perioder.first().til == null
    }

    private fun hentAktivPeriode(bruker: Bruker): ArbeidssokerperiodeDto? {
        val perioder = repository.hentPerioder(bruker.gjeldendeFoedselsnummer, bruker.historiskeFoedselsnummer)

        if (perioder.isEmpty()) return null

        return perioder.find { it.til == null }
    }
}
