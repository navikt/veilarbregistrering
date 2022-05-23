package no.nav.fo.veilarbregistrering.bruker.resources

import no.nav.fo.veilarbregistrering.autorisasjon.DefaultAutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoMapper.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/person")
class KontaktinfoResource(
    private val userService: UserService,
    private val kontaktinfoService: KontaktinfoService,
    private val autorisasjonService: DefaultAutorisasjonService
) : KontaktinfoApi {

    @GetMapping("/kontaktinfo")
    override fun hentKontaktinfo(): KontaktinfoDto? {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        val kontaktinfo = kontaktinfoService.hentKontaktinfo(bruker)
        return map(kontaktinfo)
    }
}