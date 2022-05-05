package no.nav.fo.veilarbregistrering.bruker

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.common.auth.Constants
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.bruker.Bruker.Companion.of
import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.fo.veilarbregistrering.config.isDevelopment
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val authContextHolder: AuthContextHolder,
    enableSyntetiskeFnr: Boolean = false
) {
    init {
        if (enableSyntetiskeFnr) {
            logger.warn("Enabler syntetiske fødselsnummer")
            FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = true;
        } else {
            logger.warn("Disabler syntetiske fødselsnummer")
            FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = false;
        }
    }

    fun finnBrukerGjennomPdl(): Bruker = finnBrukerGjennomPdl(hentFnrFraUrlEllerToken())
    fun finnBrukerGjennomPdl(fnr: Foedselsnummer): Bruker = map(pdlOppslagGateway.hentIdenter(fnr))

    fun hentBruker(aktorId: AktorId): Bruker = map(pdlOppslagGateway.hentIdenter(aktorId))

    fun getEnhetIdFromUrlOrThrow(): String =
        servletRequest().getParameter("enhetId") ?: throw ManglendeBrukerInfoException("Mangler enhetId")

    private fun hentFnrFraUrlEllerToken(): Foedselsnummer {
        val fnr: String =
            servletRequest().getParameter("fnr")
            ?: authContextHolder.subject.orElseThrow { IllegalArgumentException() }
        if (isDevelopment()) {
            logger.info("PID i token: ${authContextHolder.hentFnrFraPid()}")
            logger.info("Fødselsnummer hentet fra token eller URL: $fnr")
        }
        if (!FodselsnummerValidator.isValid(fnr)) {
            throw ManglendeBrukerInfoException("Fødselsnummer hentet fra URL eller subject i token er ikke gyldig. Kan ikke gjøre oppslag i PDL.")
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

fun AuthContextHolder.hentFnrFraPid(): Optional<String> {
    return idTokenClaims.flatMap { getStringClaim(it, Constants.ID_PORTEN_PID_CLAIM) }
}