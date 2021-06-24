package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.common.featuretoggle.UnleashClient;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class RegistreringResource implements RegistreringApi {

    private static final Logger LOG = LoggerFactory.getLogger(RegistreringResource.class);

    private final UnleashClient unleashClient;
    private final BrukerRegistreringService brukerRegistreringService;
    private final SykmeldtRegistreringService sykmeldtRegistreringService;
    private final HentRegistreringService hentRegistreringService;
    private final AutorisasjonService autorisasjonsService;
    private final UserService userService;
    private final StartRegistreringStatusService startRegistreringStatusService;
    private final InaktivBrukerService inaktivBrukerService;

    public RegistreringResource(
            AutorisasjonService autorisasjonsService,
            UserService userService,
            BrukerRegistreringService brukerRegistreringService,
            HentRegistreringService hentRegistreringService,
            UnleashClient unleashClient,
            SykmeldtRegistreringService sykmeldtRegistreringService,
            StartRegistreringStatusService startRegistreringStatusService,
            InaktivBrukerService inaktivBrukerService) {
        this.autorisasjonsService = autorisasjonsService;
        this.userService = userService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.hentRegistreringService = hentRegistreringService;
        this.unleashClient = unleashClient;
        this.sykmeldtRegistreringService = sykmeldtRegistreringService;
        this.startRegistreringStatusService = startRegistreringStatusService;
        this.inaktivBrukerService = inaktivBrukerService;
    }

    @Override
    @GetMapping("/startregistrering")
    public StartRegistreringStatusDto hentStartRegistreringStatus() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        autorisasjonsService.sjekkLesetilgangMedAktorId(bruker.getAktorId());
        return startRegistreringStatusService.hentStartRegistreringStatus(bruker);
    }

    @Override
    @PostMapping("/startregistrering")
    public OrdinaerBrukerRegistrering registrerBruker(@RequestBody OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();
        autorisasjonsService.sjekkSkrivetilgangMedAktorId(bruker.getAktorId());

        NavVeileder veileder = navVeileder();

        ordinaerBrukerRegistrering.setOpprettetDato(LocalDateTime.now());

        OrdinaerBrukerRegistrering opprettetRegistrering = brukerRegistreringService.registrerBrukerUtenOverforing(ordinaerBrukerRegistrering, bruker, veileder);

        brukerRegistreringService.overforArena(opprettetRegistrering.getId(), bruker, veileder);

        return opprettetRegistrering;
    }

    @Override
    @GetMapping("/registrering")
    public ResponseEntity<BrukerRegistreringWrapper> hentRegistrering() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();
        autorisasjonsService.sjekkLesetilgangMedAktorId(bruker.getAktorId());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = hentRegistreringService.hentOrdinaerBrukerRegistrering(bruker);
        SykmeldtRegistrering sykmeldtBrukerRegistrering = hentRegistreringService.hentSykmeldtRegistrering(bruker);

        BrukerRegistreringWrapper brukerRegistreringWrapper = BrukerRegistreringWrapperFactory.create(ordinaerBrukerRegistrering, sykmeldtBrukerRegistrering);
        if (brukerRegistreringWrapper == null) {
            LOG.info("Bruker ble ikke funnet i databasen.");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(brukerRegistreringWrapper);
    }

    @Override
    @GetMapping("/igangsattregistrering")
    public ResponseEntity<BrukerRegistreringWrapper> hentPaabegyntRegistrering() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();
        autorisasjonsService.sjekkLesetilgangMedAktorId(bruker.getAktorId());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = hentRegistreringService.hentIgangsattOrdinaerBrukerRegistrering(bruker);

        BrukerRegistreringWrapper brukerRegistreringWrapper = BrukerRegistreringWrapperFactory.create(ordinaerBrukerRegistrering, null);
        if (brukerRegistreringWrapper == null) {
            LOG.info("Bruker ble ikke funnet i databasen.");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(brukerRegistreringWrapper);
    }

    @Override
    @PostMapping("/startreaktivering")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reaktivering() {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();
        autorisasjonsService.sjekkSkrivetilgangTilBruker(bruker.getGjeldendeFoedselsnummer());

        inaktivBrukerService.reaktiverBruker(bruker, autorisasjonsService.erVeileder());
    }

    @Override
    @PostMapping("/startregistrersykmeldt")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registrerSykmeldt(@RequestBody SykmeldtRegistrering sykmeldtRegistrering) {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();
        autorisasjonsService.sjekkSkrivetilgangMedAktorId(bruker.getAktorId());

        NavVeileder veileder = navVeileder();

        sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker, veileder);
    }

    private NavVeileder navVeileder() {
        if (!autorisasjonsService.erVeileder()) {
            return null;
        }

        return new NavVeileder(
                autorisasjonsService.getInnloggetVeilederIdent(),
                userService.getEnhetIdFromUrlOrThrow());
    }

    private boolean tjenesteErNede() {
        return unleashClient.isEnabled("arbeidssokerregistrering.nedetid");
    }

}