package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ReaktiveringResource(
    private val autorisasjonsService: AutorisasjonService,
    private val userService: UserService,
    private val unleashClient: UnleashClient,
    private val tilgangskontrollService: TilgangskontrollService,
    private val reaktiveringBrukerService: ReaktiveringBrukerService
) : ReaktiveringApi {

    @PostMapping("/startreaktivering")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun reaktivering() {
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }

        val bruker = userService.finnBrukerGjennomPdl()

        var nyTilgangskontrollSierOK = true
        if (unleashClient.isEnabled("veilarbregistrering.ny-tilgangskontroll")) {
            nyTilgangskontrollSierOK = try {
                tilgangskontrollService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
                true
            } catch (e: Exception) {
                logger.info("Ny tilgangskontroll avviste tilgang til bruker.", e)
                false
            }
        }
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        if (!nyTilgangskontrollSierOK) {
            logger.info("Avvik mellom ny og gammel tilgangskontroll: Gammel sier OK, ny feiler (lesetilgang)")
        }

        reaktiveringBrukerService.reaktiverBruker(bruker, autorisasjonsService.erVeileder())
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}