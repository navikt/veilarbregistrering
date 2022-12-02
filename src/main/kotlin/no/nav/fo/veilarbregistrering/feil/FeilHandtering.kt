package no.nav.fo.veilarbregistrering.feil

import no.nav.fo.veilarbregistrering.arbeidsforhold.ForbiddenException
import no.nav.fo.veilarbregistrering.arbeidsforhold.HentArbeidsforholdException
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.UnauthorizedException
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonException
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonLevel3Exception
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonValideringException
import no.nav.fo.veilarbregistrering.bruker.feil.*
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerTekniskException
import no.nav.fo.veilarbregistrering.oppfolging.SammensattOppfolgingStatusException
import no.nav.fo.veilarbregistrering.oppgave.OppgaveAlleredeOpprettet
import no.nav.fo.veilarbregistrering.registrering.reaktivering.KanIkkeReaktiveresException
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class FeilHandtering : ResponseEntityExceptionHandler() {

    @ExceptionHandler(AktiverBrukerException::class)
    fun aktiverBrukerException(f: AktiverBrukerException): ResponseEntity<FeilDto> {
        logger.warn(f.feilmelding)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(FeilDto(f.aktiverBrukerFeil.toString()))
    }

    @ExceptionHandler(AktiverBrukerTekniskException::class)
    fun aktiverBrukerException(f: AktiverBrukerTekniskException): ResponseEntity<Any> {
        logger.error(f)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build()
    }

    @ExceptionHandler(KontaktinfoIngenTilgang::class)
    fun kontaktInfoIngenTilgang(feil: KontaktinfoIngenTilgang) =
        ResponseEntity.status(FORBIDDEN).body(feil.message)

    @ExceptionHandler(KontaktinfoIngenTreff::class)
    fun kontaktInfoIngenTreff(feil: KontaktinfoIngenTreff) =
        ResponseEntity.status(NOT_FOUND).body(feil.message)

    @ExceptionHandler(KontaktinfoUkjentFeil::class)
    fun kontaktInfoUkjentFeil(feil: KontaktinfoUkjentFeil) =
        ResponseEntity.status(INTERNAL_SERVER_ERROR).body(feil.message)

    @ExceptionHandler(OppgaveAlleredeOpprettet::class)
    fun handleOppgaveAlleredeOpprettet(feil: OppgaveAlleredeOpprettet): ResponseEntity<Any> {
        logger.warn(feil.message ?: "Uventet feil", feil)
        return ResponseEntity.status(FORBIDDEN).body(feil.message)
    }

    @ExceptionHandler(HentIdenterException::class)
    fun handleHentIdenterException(feil: HentIdenterException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(feil.message)
    }

    @ExceptionHandler(ManglendeBrukerInfoException::class)
    fun handleManglendeBrukerInfoException(feil: ManglendeBrukerInfoException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(UNAUTHORIZED).body(feil.message)
    }

    @ExceptionHandler(KanIkkeReaktiveresException::class)
    fun handleKanIkkeReaktiveresException(feil: KanIkkeReaktiveresException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(BAD_REQUEST).body(feil.message)
    }

    @ExceptionHandler(SammensattOppfolgingStatusException::class)
    fun handleSammensattOppfolgingStatusException(feil: SammensattOppfolgingStatusException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(feil.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(feil: ForbiddenException): ResponseEntity<String> {
        logger.warn(feil.message, feil)
        return ResponseEntity.status(FORBIDDEN).body(feil.message)
    }

    @ExceptionHandler(HentArbeidsforholdException::class)
    fun handleHentArbeidsforholdException(feil: HentArbeidsforholdException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(feil.message)
    }

    @ExceptionHandler(AutorisasjonException::class)
    fun handleAutorisasjonException(feil: AutorisasjonException): ResponseEntity<String> {
        logger.warn(feil.message, feil)
        return ResponseEntity.status(FORBIDDEN).body(feil.message)
    }

    @ExceptionHandler(AutorisasjonLevel3Exception::class)
    fun handleAutorisasjonLevel3Exception(feil: AutorisasjonLevel3Exception): ResponseEntity<String> {
        logger.info(feil.message, feil)
        return ResponseEntity.status(FORBIDDEN).body(feil.message)
    }

    @ExceptionHandler(AutorisasjonValideringException::class)
    fun handleAutorisasjonValideringException(feil: AutorisasjonValideringException): ResponseEntity<String> {
        logger.error(feil.message, feil)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(feil.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(feil: UnauthorizedException): ResponseEntity<String> {
        logger.warn(feil.message, feil)
        return ResponseEntity.status(UNAUTHORIZED).body(feil.message)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(r: RuntimeException): ResponseEntity<Any> {
        logger.error(r.message ?: "Uventet feil", r)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build()
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException::class)
    fun handleUnsupportedMediaTypeStatusException(e: UnsupportedMediaTypeStatusException):ResponseEntity<Any> {
        logger.warn("Feil media-type i request: ${e.message}", e)
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE).build()
    }
}
