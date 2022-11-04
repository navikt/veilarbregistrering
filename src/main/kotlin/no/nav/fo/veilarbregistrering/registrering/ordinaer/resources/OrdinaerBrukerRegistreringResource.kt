package no.nav.fo.veilarbregistrering.registrering.ordinaer.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.veileder.NavVeilederService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class OrdinaerBrukerRegistreringResource(
    private val tilgangskontrollService: TilgangskontrollService,
    private val userService: UserService,
    private val brukerRegistreringService: BrukerRegistreringService,
    private val navVeilederService: NavVeilederService,
    private val unleashClient: UnleashClient
) : OrdinaerBrukerRegistreringApi {

    @PostMapping(path=["/startregistrering", "/fullfoerordinaerregistrering"])
    override fun registrerBruker(@RequestBody ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): OrdinaerBrukerRegistrering {
        if (tjenesteErNede()) {
            brukerRegistreringService.registrerAtArenaHarPlanlagtNedetid()
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val veileder = navVeilederService.navVeileder()
        val opprettetRegistrering =
            brukerRegistreringService.registrerBrukerUtenOverforing(ordinaerBrukerRegistrering, bruker, veileder)
        brukerRegistreringService.overforArena(opprettetRegistrering.id, bruker, veileder)
        return opprettetRegistrering
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}