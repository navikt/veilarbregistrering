package no.nav.fo.veilarbregistrering.profilering.resources

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ProfileringResource(
    private val userService: UserService,
    private val autorisasjonService: AutorisasjonService,
    private val oppfolgingGateway: OppfolgingGateway,
    private val profileringRepository: ProfileringRepository,
    private val hentRegistreringService: HentRegistreringService,
) : ProfileringApi {

    @GetMapping("/profilering")
    override fun hentProfileringForBurker(): ProfileringDto {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(bruker.gjeldendeFoedselsnummer)

        if (oppfolgingsstatus.servicegruppe?.value() != "IVURD") {
            // returner basert på servicegruppe
        }

        val brukerregistrering = hentRegistreringService.hentBrukerregistrering(bruker)

        if (brukerregistrering != null) {
            val profilering = profileringRepository.hentProfileringForId(brukerregistrering.registrering.id)
            // return innsatsgruppe
        }

        // returner default
    }
}
