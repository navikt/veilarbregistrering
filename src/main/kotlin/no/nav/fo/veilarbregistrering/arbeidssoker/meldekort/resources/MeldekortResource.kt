package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.resources

import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortService
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/arbeidssoker")
class MeldekortResource(
    private val userService: UserService,
    private val tilgangskontrollService: TilgangskontrollService,
    private val meldekortService: MeldekortService
) : MeldekortApi {
    @GetMapping("/meldekort")
    override fun hentMeldekort(): List<MeldekortDto> {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBrukerMedNivå3(bruker, "meldekort")
        return meldekortService.hentMeldekort(bruker.gjeldendeFoedselsnummer)
            .map(MeldekortDto::map)
    }

    @GetMapping("/meldekort/siste")
    override fun hentSisteMeldekort(): ResponseEntity<MeldekortDto> {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBrukerMedNivå3(bruker, "siste meldekort")
        return meldekortService.hentSisteMeldekort(bruker.gjeldendeFoedselsnummer)?.let {
            ResponseEntity.ok(MeldekortDto.map(it))
        } ?: ResponseEntity.noContent().build()
    }
}
