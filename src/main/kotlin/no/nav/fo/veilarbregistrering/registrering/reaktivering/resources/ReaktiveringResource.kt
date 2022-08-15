package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ReaktiveringResource(
    private val autorisasjonsService: AutorisasjonService,
    private val userService: UserService,
    private val unleashClient: UnleashClient,
    private val reaktiveringBrukerService: ReaktiveringBrukerService
) : ReaktiveringApi {

    @PostMapping("/startreaktivering")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun reaktivering() {
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }

        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        reaktiveringBrukerService.reaktiverBruker(bruker, autorisasjonsService.erVeileder())
    }

    @GetMapping("/kanreaktiveres")
    override fun kanReaktiveres(): KanReaktiveresResponseDto {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return KanReaktiveresResponseDto(reaktiveringBrukerService.kanReaktiveres(bruker))
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}