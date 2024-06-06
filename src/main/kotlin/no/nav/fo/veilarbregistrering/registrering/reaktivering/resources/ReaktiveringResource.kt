package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import io.getunleash.Unleash
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.resources.Fnr
import no.nav.fo.veilarbregistrering.autorisasjon.CefMelding
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.resources.EndpointRemovedException
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ReaktiveringResource(
    private val userService: UserService,
    private val unleashClient: Unleash,
    private val tilgangskontrollService: TilgangskontrollService,
    private val reaktiveringBrukerService: ReaktiveringBrukerService
) : ReaktiveringApi {

    @PostMapping("/fullfoerreaktivering")
    @ResponseStatus(HttpStatus.GONE)
    override fun reaktivering() {
        throw EndpointRemovedException("Endepunktet er ikke lenger i bruk.")
        /*
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }

        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkSkrivetilgangTilBruker(bruker, "reaktivering")

        reaktiveringBrukerService.reaktiverBruker(bruker, tilgangskontrollService.erVeileder())
         */
    }

    @PostMapping("/fullfoerreaktivering/systembruker")
    @ResponseStatus(HttpStatus.GONE)
    override fun reaktiveringMedSystembruker(@RequestBody fnr: Fnr) {
        throw EndpointRemovedException("Endepunktet er ikke lenger i bruk.")
        /*
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }

        val bruker = userService.finnBrukerGjennomPdl(Foedselsnummer(fnr.fnr))
        tilgangskontrollService.sjekkSkrivetilgangTilBrukerForSystem(bruker.gjeldendeFoedselsnummer, CefMelding("System forsøker å reaktivere bruker med fødselsnummer=${bruker.gjeldendeFoedselsnummer.foedselsnummer} leser egen meldekort informasjon", bruker.gjeldendeFoedselsnummer))

        reaktiveringBrukerService.reaktiverBruker(bruker, false)
         */
    }

    @PostMapping("/kan-reaktiveres")
    @ResponseStatus(HttpStatus.GONE)
    override fun kanReaktiveres(@RequestBody fnr: Fnr): KanReaktiveresDto {
        throw EndpointRemovedException("Endepunktet er ikke lenger i bruk.")
        /*
        val bruker = userService.finnBrukerGjennomPdl(Foedselsnummer(fnr.fnr))
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker, "reaktivering")

        return KanReaktiveresDto(kanReaktiveres = reaktiveringBrukerService.kanReaktiveres(bruker))
         */
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}
