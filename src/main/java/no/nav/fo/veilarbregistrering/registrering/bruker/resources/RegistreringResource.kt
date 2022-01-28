package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerException
import no.nav.fo.veilarbregistrering.oppfolging.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.BrukerRegistreringWrapperFactory.create
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class RegistreringResource(
    private val autorisasjonsService: AutorisasjonService,
    private val userService: UserService,
    private val brukerRegistreringService: BrukerRegistreringService,
    private val hentRegistreringService: HentRegistreringService,
    private val unleashClient: UnleashClient,
    private val sykmeldtRegistreringService: SykmeldtRegistreringService,
    private val startRegistreringStatusService: StartRegistreringStatusService,
    private val inaktivBrukerService: InaktivBrukerService
) : RegistreringApi {
    @GetMapping("/startregistrering")
    override fun hentStartRegistreringStatus(): StartRegistreringStatusDto {
        val bruker = userService.finnBrukerGjennomPdl()

        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.aktorId)

        return startRegistreringStatusService.hentStartRegistreringStatus(bruker)
    }

    @PostMapping("/startregistrering-test")
    fun registrerBruker() {
        throw AktiverBrukerException(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE)
    }

    @PostMapping("/startregistrering")
    override fun registrerBruker(@RequestBody ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): OrdinaerBrukerRegistrering {
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.aktorId)

        val veileder = navVeileder()
        val opprettetRegistrering =
            brukerRegistreringService.registrerBrukerUtenOverforing(ordinaerBrukerRegistrering, bruker, veileder)
        brukerRegistreringService.overforArena(opprettetRegistrering.id, bruker, veileder)
        return opprettetRegistrering
    }

    @GetMapping("/registrering")
    override fun hentRegistrering(): ResponseEntity<BrukerRegistreringWrapper> {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.aktorId)
        val ordinaerBrukerRegistrering = hentRegistreringService.hentOrdinaerBrukerRegistrering(bruker)
        val sykmeldtBrukerRegistrering = hentRegistreringService.hentSykmeldtRegistrering(bruker)
        val brukerRegistreringWrapper = create(ordinaerBrukerRegistrering, sykmeldtBrukerRegistrering)
        if (brukerRegistreringWrapper == null) {
            LOG.info("Bruker ble ikke funnet i databasen.")
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(brukerRegistreringWrapper)
    }

    @GetMapping("/igangsattregistrering")
    override fun hentPaabegyntRegistrering(): ResponseEntity<BrukerRegistreringWrapper> {
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.aktorId)
        val ordinaerBrukerRegistrering = hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(bruker)
        val brukerRegistreringWrapper = create(ordinaerBrukerRegistrering, null)
        if (brukerRegistreringWrapper == null) {
            LOG.info("Bruker ble ikke funnet i databasen.")
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(brukerRegistreringWrapper)
    }

    @PostMapping("/startreaktivering")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun reaktivering() {
        if (tjenesteErNede()) {
            throw RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.")
        }
        val bruker = userService.finnBrukerGjennomPdl()
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        inaktivBrukerService.reaktiverBruker(bruker, autorisasjonsService.erVeileder())
    }

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
            userService.enhetIdFromUrlOrThrow
        )
    }

    private fun tjenesteErNede(): Boolean {
        return unleashClient.isEnabled("arbeidssokerregistrering.nedetid")
    }

    companion object {
        private val LOG = loggerFor<RegistreringResource>()
    }
}