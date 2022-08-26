package no.nav.fo.veilarbregistrering.registrering.veileder

import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService

class NavVeilederService(
    private val tilgangskontrollService: TilgangskontrollService,
    private val userService: UserService,
) {

    fun navVeileder(): NavVeileder? {
        if (!tilgangskontrollService.erVeileder()) { return null }
        return NavVeileder(
            tilgangskontrollService.innloggetVeilederIdent,
            userService.getEnhetIdFromUrlOrThrow())
    }
}