package no.nav.fo.veilarbregistrering.helsesjekk.resources

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.ws.rs.GET

@RestController
@RequestMapping("/internal")
class HelsesjekkResource {

    @GetMapping("/isAlive")
    fun isAlive() {
    }

    @GET
    @GetMapping("/isReady")
    fun isReady() {
    }

}