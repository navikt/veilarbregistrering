package no.nav.fo.veilarbregistrering.arbeidsledigDato.resources



import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate


@RestController
@RequestMapping("/api")
class ArbeidsledigDatoResource(
    private val autorisasjonService: AutorisasjonService,
    private val userService: UserService,
) : ArbeidsledigDatoApi {

    @GetMapping("/arbeidsledigDato")
    override fun hentArbeidsledigDato(): ArbeidsledigDatoDto? {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        return ArbeidsledigDatoDto(dato=null)
    }

    @PostMapping("/arbeidsledigDato")
    override fun lagreArbeidsledigDato(dato: LocalDate): ArbeidsledigDatoDto {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        return ArbeidsledigDatoDto(dato=null)
    }
}
