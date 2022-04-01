package no.nav.fo.veilarbregistrering.feil

import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.HentArbeidsforholdException
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonException
import no.nav.fo.veilarbregistrering.bruker.feil.*
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.adapter.veilarbarena.SammensattOppfolgingStatusException
import no.nav.fo.veilarbregistrering.oppgave.OppgaveAlleredeOpprettet
import no.nav.fo.veilarbregistrering.registrering.bruker.KanIkkeReaktiveresException
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
    fun handleOppgaveAlleredeOpprettet(feil: OppgaveAlleredeOpprettet): ResponseEntity<Any> {
        logger.warn(feil.message ?: "Uventet feil", feil)
        return ResponseEntity.status(FORBIDDEN)
            .body(feil.message)
    }

    @ExceptionHandler(HentIdenterException::class)
    fun handleHentIdenterException(feil: HentIdenterException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(feil.message)
    }

    @ExceptionHandler(ManglendeBrukerInfoException::class)
    fun handleManglendeBrukerInfoException(feil: ManglendeBrukerInfoException) =
        ResponseEntity.status(UNAUTHORIZED)
            .body(feil.message)

    @ExceptionHandler(KanIkkeReaktiveresException::class)
    fun handleKanIkkeReaktiveresException(feil: KanIkkeReaktiveresException) =
        ResponseEntity.status(BAD_REQUEST)
            .body(feil.message)

    @ExceptionHandler(SammensattOppfolgingStatusException::class)
    fun handleSammensattOppfolgingStatusException(feil: SammensattOppfolgingStatusException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(feil.message)
    }

    @ExceptionHandler(HentArbeidsforholdException::class)
    fun handleSammensattOppfolgingStatusException(feil: HentArbeidsforholdException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(feil.message)
    }

    @ExceptionHandler(AutorisasjonException::class)
    fun handleAutorisasjonException(feil: AutorisasjonException) : ResponseEntity<Any> {
        logger.warn(feil.message, feil)
        return ResponseEntity.status(FORBIDDEN).body(feil.message)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(r: RuntimeException): ResponseEntity<Any> {
        logger.error(r.message ?: "Uventet feil", r)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build()
    }
}
