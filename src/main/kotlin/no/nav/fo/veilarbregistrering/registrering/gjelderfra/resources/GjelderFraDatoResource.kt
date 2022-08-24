package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registrering")
class GjelderFraDatoResource(
    private val tilgangskontrollService: TilgangskontrollService,
    private val userService: UserService,
    private val gjelderFraService: GjelderFraService
) : GjelderFraDatoApi {

    @GetMapping("/gjelderfra")
    override fun hentGjelderFraDato(): GjelderFraDatoResponseDto? {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val gjelderFraDato = gjelderFraService.hentDato(bruker)
        return GjelderFraDatoResponseDto.fra(gjelderFraDato)
    }

    @PostMapping("/gjelderfra")
    override fun lagreGjelderFraDato(@RequestBody datoDto: GjelderFraDatoRequestDto): ResponseEntity<Nothing> {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        gjelderFraService.opprettDato(bruker, datoDto.dato)
        return ResponseEntity.status(HttpStatus.CREATED).body(null)
    }
}
