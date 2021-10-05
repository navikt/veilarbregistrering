package no.nav.fo.veilarbregistrering.bruker.resources

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class InternalUserResource(private val userService: UserService) {

    @GetMapping("/bruker")
    fun fnrOgAktorIdOppslag(@RequestParam(required = false) fnr: String?, @RequestParam(required=false) aktorid: String?): Bruker =
        fnr?.let {
            userService.finnBrukerGjennomPdl(Foedselsnummer.of(fnr))
        } ?: aktorid?.let {
            userService.hentBruker(AktorId.of(aktorid))
        } ?: throw IllegalArgumentException("Må ha enten fnr=X eller aktorid=Y")
}