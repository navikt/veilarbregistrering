package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.resources.Fnr
import no.nav.fo.veilarbregistrering.autorisasjon.CefMelding
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.config.requireClusterName
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringBrukerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ReaktiveringResource(
    private val userService: UserService,
    private val unleashClient: UnleashClient,
    private val tilgangskontrollService: TilgangskontrollService,
    private val reaktiveringBrukerService: ReaktiveringBrukerService
) : ReaktiveringApi {

    @PostMapping(path=["/startreaktivering", "/fullfoerreaktivering"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun reaktivering() {
        if (unleashClient.isEnabled("veilarbregistrering.fullfoer-registrering.off")) {
            throw RuntimeException("POST for å fullføre registrering er ikke støttet i ${requireClusterName()}")
        }

        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }

        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        reaktiveringBrukerService.reaktiverBruker(bruker, tilgangskontrollService.erVeileder())
    }

    @PostMapping("/fullfoerreaktivering/systembruker")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun reaktiveringMedSystembruker(@RequestBody fnr: Fnr) {
        if (unleashClient.isEnabled("veilarbregistrering.fullfoer-registrering.off")) {
            throw RuntimeException("POST for å fullføre registrering er ikke støttet i ${requireClusterName()}")
        }

        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }

        val bruker = userService.finnBrukerGjennomPdl(Foedselsnummer(fnr.fnr))
        tilgangskontrollService.sjekkSkrivetilgangTilBrukerForSystem(bruker.gjeldendeFoedselsnummer, CefMelding("System forsøker å reaktivere bruker med fødselsnummer=${bruker.gjeldendeFoedselsnummer.foedselsnummer} leser egen meldekort informasjon", bruker.gjeldendeFoedselsnummer))

        reaktiveringBrukerService.reaktiverBruker(bruker, false)
    }

    @PostMapping("/kan-reaktiveres")
    override fun kanReaktiveres(@RequestBody fnr: Fnr): KanReaktiveresDto {
        val bruker = userService.finnBrukerGjennomPdl(Foedselsnummer(fnr.fnr))
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)

        return KanReaktiveresDto(kanReaktiveres = reaktiveringBrukerService.kanReaktiveres(bruker))
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}