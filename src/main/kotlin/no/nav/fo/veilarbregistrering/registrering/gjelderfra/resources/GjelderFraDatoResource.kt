package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources



import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate


@RestController
@RequestMapping("/api/registrering")
class GjelderFraDatoResource(
    private val autorisasjonService: AutorisasjonService,
    private val userService: UserService,
) : GjelderFraDatoApi {

    @GetMapping("/gjelder-fra")
    override fun hentGjelderFraDato(): GjelderFraDatoDto? {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return GjelderFraDatoDto(dato=null)
    }

    @PostMapping("/gjelder-fra")
    override fun lagreGjelderFraDato(dato: LocalDate): GjelderFraDatoDto {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return GjelderFraDatoDto(dato=null)
    }
}
