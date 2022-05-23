package no.nav.fo.veilarbregistrering.arbeidsforhold.resources

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ArbeidsforholdResource(
    private val autorisasjonService: AutorisasjonService,
    private val userService: UserService,
    private val arbeidsforholdGateway: ArbeidsforholdGateway,
) : ArbeidsforholdApi {

    @GetMapping("/sistearbeidsforhold")
    override fun hentSisteArbeidsforhold(): ArbeidsforholdDto? {
        val bruker = userService.finnBrukerGjennomPdl()

        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val flereArbeidsforhold: FlereArbeidsforhold =
            arbeidsforholdGateway.hentArbeidsforhold(bruker.gjeldendeFoedselsnummer)
        return ArbeidsforholdDto.fra(flereArbeidsforhold.siste())
    }
}