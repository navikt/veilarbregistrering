package no.nav.fo.veilarbregistrering.registrering.sykmeldt.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.registrering.bruker.NavVeileder
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistreringService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class SykmeldtResource(
    private val autorisasjonsService: AutorisasjonService,
    private val userService: UserService,
    private val unleashClient: UnleashClient,
    private val sykmeldtRegistreringService: SykmeldtRegistreringService
) : SykmeldtApi {

    @PostMapping("/startregistrersykmeldt")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun registrerSykmeldt(@RequestBody sykmeldtRegistrering: SykmeldtRegistrering) {
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.aktorId)
        val veileder = navVeileder()
        sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker, veileder)
    }

    private fun navVeileder(): NavVeileder? {
        return if (!autorisasjonsService.erVeileder()) {
            null
        } else NavVeileder(
            autorisasjonsService.innloggetVeilederIdent,
            userService.getEnhetIdFromUrlOrThrow()
        )
    }

    private fun tjenesteErNede(): Boolean = unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
}