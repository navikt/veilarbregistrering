package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registrering")
class GjelderFraDatoResource(
    private val autorisasjonService: AutorisasjonService,
    private val userService: UserService,
    private val gjelderFraService: GjelderFraService
) : GjelderFraDatoApi {

    @GetMapping("/gjelder-fra")
    override fun hentGjelderFraDato(): GjelderFraDatoResponseDto? {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return GjelderFraDatoResponseDto.fra(gjelderFraService.hentDato(bruker))
    }

    @PostMapping("/gjelder-fra")
    override fun lagreGjelderFraDato(@RequestBody datoDto: GjelderFraDatoRequestDto): ResponseEntity<Nothing> {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        gjelderFraService.opprettDato(bruker, datoDto.dato)
        return ResponseEntity.status(HttpStatus.CREATED).body(null)
    }
}
