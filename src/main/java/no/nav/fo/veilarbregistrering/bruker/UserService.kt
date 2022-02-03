package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker.Companion.of
import no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest
import no.nav.common.auth.context.AuthContextHolder
import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException
import java.lang.IllegalArgumentException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val authContextHolder: AuthContextHolder
) {
    fun finnBrukerGjennomPdl(): Bruker = finnBrukerGjennomPdl(hentFnrFraUrlEllerToken())

    fun finnBrukerGjennomPdl(fnr: Foedselsnummer?): Bruker {
        return map(pdlOppslagGateway.hentIdenter(fnr!!))
    }

    fun hentBruker(aktorId: AktorId?): Bruker {
        return map(pdlOppslagGateway.hentIdenter(aktorId!!))
    }

    fun getEnhetIdFromUrlOrThrow(): String =
        servletRequest().getParameter("enhetId") ?: throw ManglendeBrukerInfoException("Mangler enhetId")

    private fun hentFnrFraUrlEllerToken(): Foedselsnummer {
        val fnr: String =
            servletRequest().getParameter("fnr")
            ?: authContextHolder.subject.orElseThrow { IllegalArgumentException() }

        if (!FodselsnummerValidator.isValid(fnr)) {
            throw ManglendeBrukerInfoException("Fødselsnummer ikke gyldig.")
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