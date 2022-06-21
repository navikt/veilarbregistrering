package no.nav.fo.veilarbregistrering.registrering.ordinaer.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.bruker.NavVeileder
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class OrdinaerBrukerRegistreringResource(
    private val autorisasjonsService: AutorisasjonService,
    private val userService: UserService,
    private val brukerRegistreringService: BrukerRegistreringService,
    private val unleashClient: UnleashClient
) : OrdinaerBrukerRegistreringApi {

    @PostMapping("/startregistrering")
    override fun registrerBruker(@RequestBody ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): OrdinaerBrukerRegistrering {
        if (tjenesteErNede()) {
            brukerRegistreringService.registrerAtArenaHarPlanlagtNedetid()
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val veileder = navVeileder()
        val opprettetRegistrering =
            brukerRegistreringService.registrerBrukerUtenOverforing(ordinaerBrukerRegistrering, bruker, veileder)
        brukerRegistreringService.overforArena(opprettetRegistrering.id, bruker, veileder)
        return opprettetRegistrering
    }

    private fun navVeileder(): NavVeileder? {
        return if (!autorisasjonsService.erVeileder()) {
            null
        } else NavVeileder(
            autorisasjonsService.innloggetVeilederIdent,
            userService.getEnhetIdFromUrlOrThrow()
        )
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}