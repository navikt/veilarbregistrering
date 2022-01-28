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
    fun fnrOgAktorIdOppslag(@RequestParam(required = false) fnr: String?, @RequestParam(required=false) aktorid: String?): User =
        fnr?.let {
            User(it, userService.finnBrukerGjennomPdl(Foedselsnummer.of(fnr)).aktorId.aktorId)
        } ?: aktorid?.let {
            User(userService.hentBruker(AktorId(aktorid)).gjeldendeFoedselsnummer.stringValue(), it)
        } ?: throw IllegalArgumentException("Må ha enten fnr=X eller aktorid=Y")
}

data class User(val fnr: String, val aktorid: String)