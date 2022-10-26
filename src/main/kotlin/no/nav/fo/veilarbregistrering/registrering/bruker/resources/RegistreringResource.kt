package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringStatusService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RegistreringResource(
    private val tilgangskontrollService: TilgangskontrollService,
    private val userService: UserService,
    private val hentRegistreringService: HentRegistreringService,
    private val startRegistreringStatusService: StartRegistreringStatusService
) : RegistreringApi {

    @GetMapping("/startregistrering")
    override fun hentStartRegistreringStatus(@RequestHeader("Nav-Consumer-Id") consumerId: String): StartRegistreringStatusDto {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return startRegistreringStatusService.hentStartRegistreringStatus(bruker, consumerId)
    }

    @GetMapping("/registrering")
    override fun hentRegistrering(): ResponseEntity<BrukerRegistreringWrapper> {
        val bruker = userService.finnBrukerGjennomPdl()

        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return hentRegistreringService.hentBrukerregistrering(bruker)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.noContent().build()
    }
}