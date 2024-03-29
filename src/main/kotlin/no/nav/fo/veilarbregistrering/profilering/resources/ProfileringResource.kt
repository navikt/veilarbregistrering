package no.nav.fo.veilarbregistrering.profilering.resources

import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.profilering.ProfilertInnsatsgruppeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ProfileringResource(
    private val userService: UserService,
    private val tilgangskontrollService: TilgangskontrollService,
    private val profilertInnsatsgruppeService: ProfilertInnsatsgruppeService
) : ProfileringApi {

    @GetMapping("/profilering")
    override fun hentProfileringForBurker(): ProfileringDto {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker, "profilering")

        return ProfileringDto.fra(profilertInnsatsgruppeService.hentProfilering(bruker))
    }

    @GetMapping("/profilering/standard-innsats")
    override fun erStandardInnsatsBruker(): Boolean {
      val bruker = userService.finnBrukerGjennomPdl()
      tilgangskontrollService.sjekkLesetilgangTilBruker(bruker, "standard innsats")

      return profilertInnsatsgruppeService.erStandardInnsats(bruker)
    }
}
