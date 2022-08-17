package no.nav.fo.veilarbregistrering.arbeidsforhold.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ArbeidsforholdResource(
    private val autorisasjonService: AutorisasjonService,
    private val userService: UserService,
    private val unleashClient: UnleashClient,
    private val tilgangskontrollService: TilgangskontrollService,
    private val arbeidsforholdGateway: ArbeidsforholdGateway,
) : ArbeidsforholdApi {

    @GetMapping("/sistearbeidsforhold")
    override fun hentSisteArbeidsforhold(): ArbeidsforholdDto? {
        val bruker = userService.finnBrukerGjennomPdl()

        var nyTilgangskontrollSierOK = true
        if (unleashClient.isEnabled("veilarbregistrering.ny-tilgangskontroll")) {
            nyTilgangskontrollSierOK = try {
                tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
                true
            } catch (e: Exception) {
                false
            }
        }
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        if (!nyTilgangskontrollSierOK) {
            logger.info("Avvik mellom ny og gammel tilgangskontroll: Gammel sier OK, ny feiler (lesetilgang)")
        }
        val flereArbeidsforhold: FlereArbeidsforhold =
            arbeidsforholdGateway.hentArbeidsforhold(bruker.gjeldendeFoedselsnummer)
        return ArbeidsforholdDto.fra(flereArbeidsforhold.siste())
    }
}