package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.Pep
import no.nav.common.auth.context.AuthContextHolder
import org.springframework.web.server.ResponseStatusException
import no.nav.common.types.identer.EnhetId
import no.nav.common.abac.AbacUtils
import no.nav.common.abac.XacmlRequestBuilder
import no.nav.common.abac.constants.NavAttributter
import no.nav.common.abac.constants.StandardAttributter
import no.nav.common.abac.constants.AbacDomain
import no.nav.common.abac.domain.Attribute
import no.nav.common.abac.domain.request.*
import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import org.springframework.http.HttpStatus
import java.util.*

open class AutorisasjonService(private val veilarbPep: Pep) {
    fun skalVereInternBruker() {
        if (!AuthContextHolder.erInternBruker()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Ugyldig bruker type")
        }
    }

    fun skalVereSystemBruker() {
        if (!AuthContextHolder.erSystemBruker()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Ugyldig bruker type")
        }
    }

    fun erInternBruker(): Boolean {
        return AuthContextHolder.erInternBruker()
    }

    fun erSystemBruker(): Boolean {
        return AuthContextHolder.erSystemBruker()
    }

    fun erEksternBruker(): Boolean {
        return AuthContextHolder.erEksternBruker()
    }

    fun harTilgangTilEnhet(enhetId: String?): Boolean {
        //  ABAC feiler hvis man spør om tilgang til udefinerte enheter (null) men tillater å spørre om tilgang
        //  til enheter som ikke finnes (f.eks. tom streng)
        //  Ved å konvertere null til tom streng muliggjør vi å spørre om tilgang til enhet for brukere som
        //  ikke har enhet. Sluttbrukere da få permit mens veiledere vil få deny.
        return veilarbPep.harTilgangTilEnhet(
            innloggetBrukerToken, Optional.ofNullable(enhetId).map { enhetId: String? -> EnhetId.of(enhetId) }
                .orElse(EnhetId.of("")))
    }

    fun harTilgangTilEnhetMedSperre(enhetId: String?): Boolean {
        return veilarbPep.harTilgangTilEnhetMedSperre(innloggetBrukerToken, EnhetId.of(enhetId))
    }

    fun sjekkLesetilgangTilBruker(fnr: String) = veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.READ, Fnr(fnr))

    fun sjekkSkrivetilgangTilBruker(fnr: String) = veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.WRITE, Fnr(fnr))

    fun sjekkLesetilgangMedAktorId(aktorId: String?) {
        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.READ, AktorId.of(aktorId))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    fun sjekkSkrivetilgangMedAktorId(aktorId: String?) {
        if (!veilarbPep.harTilgangTilPerson(innloggetBrukerToken, ActionId.WRITE, AktorId.of(aktorId))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

    fun sjekkTilgangTilEnhet(enhetId: String?) {
        if (!harTilgangTilEnhet(enhetId)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
    }

    private fun lagSjekkTilgangTilNiva3Request(
        serviceUserName: String,
        userOidcToken: String,
        aktorId: String
    ): XacmlRequest {
        val oidcTokenBody = AbacUtils.extractOidcTokenBody(userOidcToken)
        val environment = XacmlRequestBuilder.lagEnvironment(serviceUserName)
        environment.attribute.add(Attribute(NavAttributter.ENVIRONMENT_FELLES_OIDC_TOKEN_BODY, oidcTokenBody))
        val action = Action()
        action.addAttribute(Attribute(StandardAttributter.ACTION_ID, ActionId.READ.name))
        val resource = Resource()
        resource.attribute.add(
            Attribute(
                NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE,
                NavAttributter.RESOURCE_VEILARB_UNDER_OPPFOLGING
            )
        )
        resource.attribute.add(Attribute(NavAttributter.RESOURCE_FELLES_DOMENE, AbacDomain.VEILARB_DOMAIN))
        resource.attribute.add(XacmlRequestBuilder.personIdAttribute(AktorId.of(aktorId)))
        val request = Request()
            .withEnvironment(environment)
            .withAction(action)
            .withResource(resource)
        return XacmlRequest().withRequest(request)
    }

    // TODO: Det er hårete å måtte skille på ekstern og intern
    //  Lag istedenfor en egen controller for interne operasjoner og en annen for eksterne
    fun hentIdentForEksternEllerIntern(queryParamFnr: String?): String {
        val fnr: String?
        fnr = if (erInternBruker()) {
            queryParamFnr
        } else if (erEksternBruker()) {
            innloggetBrukerIdent
        } else {
            // Systembruker har ikke tilgang
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (fnr == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler fnr")
        }
        return fnr
    }

    private val innloggetBrukerToken: String
        get() = AuthContextHolder.getIdTokenString()
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fant ikke token for innlogget bruker") }

    // NAV ident, fnr eller annen ID
    val innloggetBrukerIdent: String
        get() = AuthContextHolder.getSubject()
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "NAV ident is missing") }

    val innloggetVeilederIdent: String
        get() {
            if (!erInternBruker()) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }
            return innloggetBrukerIdent
        }

    fun erVeileder(): Boolean = erInternBruker()
}