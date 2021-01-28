package no.nav.fo.veilarbregistrering.feil

import no.nav.fo.veilarbregistrering.bruker.feil.*
import no.nav.fo.veilarbregistrering.oppgave.feil.OppgaveAlleredeOpprettet
import no.nav.fo.veilarbregistrering.registrering.bruker.feil.AktiverBrukerException
import no.nav.fo.veilarbregistrering.registrering.bruker.feil.KanIkkeReaktiveresException
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class FeilHandtering : ResponseEntityExceptionHandler() {

    @ExceptionHandler(AktiverBrukerException::class)
    fun aktiverBrukerException(f: AktiverBrukerException): ResponseEntity<FeilDto> =
        ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(FeilDto(f.aktiverBrukerFeil.toString()))

    @ExceptionHandler(KontaktinfoIngenTilgang::class)
    fun kontaktInfoIngenTilgang(feil: KontaktinfoIngenTilgang) =
        ResponseEntity.status(FORBIDDEN)
            .body(feil.message)

    @ExceptionHandler(KontaktinfoIngenTreff::class)
    fun kontaktInfoIngenTreff(feil: KontaktinfoIngenTreff) =
        ResponseEntity.status(NOT_FOUND)
            .body(feil.message)

    @ExceptionHandler(KontaktinfoUkjentFeil::class)
    fun kontaktInfoUkjentFeil(feil: KontaktinfoUkjentFeil) =
        ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(feil.message)

    @ExceptionHandler(OppgaveAlleredeOpprettet::class)
    fun handleOppgaveAlleredeOpprettet(feil: OppgaveAlleredeOpprettet) =
        ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(feil.message)

    @ExceptionHandler(HentIdenterException::class)
    fun handleHentIdenterException(feil: HentIdenterException) =
        ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(feil.message)

    @ExceptionHandler(ManglendeBrukerInfoException::class)
    fun handleManglendeBrukerInfoException(feil: ManglendeBrukerInfoException) =
        ResponseEntity.status(UNAUTHORIZED)
            .body(feil.message)

    @ExceptionHandler(KanIkkeReaktiveresException::class)
    fun handleKanIkkeReaktiveresException(feil: KanIkkeReaktiveresException) =
        ResponseEntity.status(BAD_REQUEST)
            .body(feil.message)
}