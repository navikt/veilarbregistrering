package no.nav.fo.veilarbregistrering.bruker

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.common.auth.Constants
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.bruker.Bruker.Companion.of
import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.secureLogger
import org.springframework.stereotype.Service

@Service
class UserService(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val authContextHolder: AuthContextHolder,
    enableSyntetiskeFnr: Boolean = false
) {
    init {
        if (enableSyntetiskeFnr) {
            logger.warn("Enabler syntetiske fødselsnummer")
            FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = true
        } else {
            logger.warn("Disabler syntetiske fødselsnummer")
            FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = false
        }
    }

    fun finnBrukerGjennomPdl(): Bruker {
        val fnr = if (authContextHolder.erEksternBruker()) {
            hentFnrFraToken()
        } else {
            hentFnrFraUrl()
        }
        return finnBrukerGjennomPdl(fnr)
    }
    fun finnBrukerGjennomPdl(fnr: Foedselsnummer): Bruker = map(pdlOppslagGateway.hentIdenter(fnr))

    fun hentBruker(aktorId: AktorId): Bruker = map(pdlOppslagGateway.hentIdenter(aktorId))

    fun getEnhetIdFromUrlOrThrow(): String =
        servletRequest().getParameter("enhetId") ?: throw ManglendeBrukerInfoException("Mangler enhetId")

    private fun hentFnrFraUrl(): Foedselsnummer {
        val fnr: String = servletRequest().getParameter("fnr")
            ?: throw IllegalStateException("Fant ikke fødselsnummer i URL for kall i veileder- eller system-kontekst. Kan ikke gjøre oppslag i PDL.")

        if (!FodselsnummerValidator.isValid(fnr)) {
            secureLogger.warn("Fødselsnummer, $fnr, hentet fra URL er ikke gyldig. Kan ikke gjøre oppslag i PDL.")
            throw ManglendeBrukerInfoException("Fødselsnummer hentet fra URL er ikke gyldig. Kan ikke gjøre oppslag i PDL.")
        }
        return Foedselsnummer(fnr)
    }

    private fun hentFnrFraToken(): Foedselsnummer {
        val fnr = authContextHolder.hentFnrFraPid()

        if (isProduction() && !FodselsnummerValidator.isValid(fnr)) {
            secureLogger.warn("Fødselsnummer, $fnr, hentet fra token er ikke gyldig. Kan ikke gjøre oppslag i PDL.")
            throw ManglendeBrukerInfoException("Fødselsnummer hentet fra token er ikke gyldig. Kan ikke gjøre oppslag i PDL.")
        }
        return Foedselsnummer(fnr)
    }

    companion object {
        private fun map(identer: Identer): Bruker {
            return of(
                identer.finnGjeldendeFnr(),
                identer.finnGjeldendeAktorId(),
                identer.finnHistoriskeFoedselsnummer()
            )
        }
    }
}

fun AuthContextHolder.hentFnrFraPid(): String {
    return idTokenClaims.flatMap { getStringClaim(it, Constants.ID_PORTEN_PID_CLAIM) }
        .orElseThrow { IllegalStateException("Fant ikke fødselsnummer i token (pid-claim) for personbruker-kontekst. Kan ikke gjøre oppslag i PDL.") }
}