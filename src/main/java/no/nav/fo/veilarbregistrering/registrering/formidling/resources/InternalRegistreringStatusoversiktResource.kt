package no.nav.fo.veilarbregistrering.registrering.formidling.resources

import no.nav.fo.veilarbregistrering.registrering.formidling.OppdaterRegistreringTilstandCommand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandService
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/internal")
class InternalRegistreringStatusoversiktResource(private val registreringTilstandService: RegistreringTilstandService) {

    @GetMapping("/statusoversikt")
    fun hentStatusoversikt(@RequestParam(required = true) statusString: String): List<Long> =
        registreringTilstandService.finnRegistreringTilstandMed(Status.parse(statusString))
            .map { it.id }

    @PutMapping("/status")
    fun oppdaterRegistreringsTilstand(
        @RequestParam(required = true) statusString: String,
        @RequestParam(required = true) id: String
    ) =
        Status.parse(statusString).let { status ->
            registreringTilstandService.oppdaterRegistreringTilstand(OppdaterRegistreringTilstandCommand.of(id, status))
        }

}