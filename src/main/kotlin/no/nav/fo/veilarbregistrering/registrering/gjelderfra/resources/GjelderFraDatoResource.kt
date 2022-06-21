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
    private val hentRegistreringService: HentRegistreringService,
    private val gjelderFraService: GjelderFraService
) : GjelderFraDatoApi {

    @GetMapping("/gjelder-fra")
    override fun hentGjelderFraDato(): GjelderFraDatoDto? {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val gjelderFraDato = gjelderFraService.hentDato(bruker)
        return GjelderFraDatoDto.fra(gjelderFraDato)
    }

    @PostMapping("/gjelder-fra")
    override fun lagreGjelderFraDato(@RequestBody datoDto: GjelderFraDatoDto): Any {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        val brukerregistrering = hentRegistreringService.hentBrukerregistreringUtenMetrics(bruker)

        if (brukerregistrering == null || datoDto.dato == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }

        return try {
            gjelderFraService.opprettDato(bruker, brukerregistrering.registrering, datoDto.dato)
            ResponseEntity.status(HttpStatus.CREATED).body(null)
        } catch(exception: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }
}
