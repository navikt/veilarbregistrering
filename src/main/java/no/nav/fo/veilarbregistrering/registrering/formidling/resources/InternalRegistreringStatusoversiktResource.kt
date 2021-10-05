package no.nav.fo.veilarbregistrering.registrering.formidling.resources

import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandService
import no.nav.fo.veilarbregistrering.registrering.formidling.Status.Companion.parse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class InternalRegistreringStatusoversiktResource(private val registreringTilstandService: RegistreringTilstandService) {

    @GetMapping("/statusoversikt")
     fun hentStatusoversikt(@RequestParam(required = true) statusString: String): List<Long> =
        registreringTilstandService.finnRegistreringTilstandMed(parse(statusString))
                .map { it.id }
}