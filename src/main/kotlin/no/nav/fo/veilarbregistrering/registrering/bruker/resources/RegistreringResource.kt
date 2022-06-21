package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringStatusService
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.BrukerRegistreringWrapperFactory.create
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class RegistreringResource(
    private val autorisasjonsService: AutorisasjonService,
    private val userService: UserService,
    private val hentRegistreringService: HentRegistreringService,
    private val startRegistreringStatusService: StartRegistreringStatusService
) : RegistreringApi {

    @GetMapping("/startregistrering")
    override fun hentStartRegistreringStatus(): StartRegistreringStatusDto {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return startRegistreringStatusService.hentStartRegistreringStatus(bruker)
    }

    @GetMapping("/registrering")
    override fun hentRegistrering(): ResponseEntity<BrukerRegistreringWrapper> {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        return hentRegistreringService.hentBrukerregistrering(bruker)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.noContent().build()
    }

    @GetMapping("/igangsattregistrering")
    override fun hentPaabegyntRegistrering(): ResponseEntity<BrukerRegistreringWrapper> {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        val ordinaerBrukerRegistrering = hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(bruker)
        val brukerRegistreringWrapper = create(ordinaerBrukerRegistrering, null)
        if (brukerRegistreringWrapper == null) {
            logger.info("Bruker ble ikke funnet i databasen.")
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(brukerRegistreringWrapper)
    }
}