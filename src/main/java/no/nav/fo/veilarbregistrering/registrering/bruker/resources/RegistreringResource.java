package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.AutentiseringUtils;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.time.LocalDateTime;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.MANUELL_REAKTIVERING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.SYKMELDT_BESVARELSE_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportFields;
import static no.nav.fo.veilarbregistrering.registrering.bruker.resources.StartRegistreringStatusMetrikker.rapporterRegistreringsstatus;

@Component
@Path("/")
@Produces("application/json")
public class RegistreringResource implements RegistreringApi {

    private static final Logger LOG = LoggerFactory.getLogger(RegistreringResource.class);

    private final UnleashService unleashService;
    private final BrukerRegistreringService brukerRegistreringService;
    private final SykmeldtRegistreringService sykmeldtRegistreringService;
    private final HentRegistreringService hentRegistreringService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final StartRegistreringStatusService startRegistreringStatusService;
    private final InaktivBrukerService inaktivBrukerService;

    public RegistreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            BrukerRegistreringService brukerRegistreringService,
            HentRegistreringService hentRegistreringService,
            UnleashService unleashService,
            SykmeldtRegistreringService sykmeldtRegistreringService,
            StartRegistreringStatusService startRegistreringStatusService,
            InaktivBrukerService inaktivBrukerService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.hentRegistreringService = hentRegistreringService;
        this.unleashService = unleashService;
        this.sykmeldtRegistreringService = sykmeldtRegistreringService;
        this.startRegistreringStatusService = startRegistreringStatusService;
        this.inaktivBrukerService = inaktivBrukerService;
    }

    @GET
    @Path("/startregistrering")
    @Override
    public StartRegistreringStatusDto hentStartRegistreringStatus() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkLesetilgangTilBruker(map(bruker));
        StartRegistreringStatusDto status = startRegistreringStatusService.hentStartRegistreringStatus(bruker);
        rapporterRegistreringsstatus(status);
        return status;
    }

    @POST
    @Path("/startregistrering")
    @Override
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        NavVeileder veileder = navVeileder();

        ordinaerBrukerRegistrering.setOpprettetDato(LocalDateTime.now());

        OrdinaerBrukerRegistrering opprettetRegistrering = brukerRegistreringService.registrerBrukerUtenOverforing(ordinaerBrukerRegistrering, bruker, veileder);

        brukerRegistreringService.overforArena(opprettetRegistrering.getId(), bruker, veileder);

        AlderMetrikker.rapporterAlder(bruker.getGjeldendeFoedselsnummer());

        return opprettetRegistrering;
    }

    @GET
    @Path("/registrering")
    @Override
    public BrukerRegistreringWrapper hentRegistrering() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = hentRegistreringService.hentOrdinaerBrukerRegistrering(bruker);
        SykmeldtRegistrering sykmeldtBrukerRegistrering = hentRegistreringService.hentSykmeldtRegistrering(bruker);

        BrukerRegistreringWrapper brukerRegistreringWrapper = BrukerRegistreringWrapperFactory.create(ordinaerBrukerRegistrering, sykmeldtBrukerRegistrering);
        if (brukerRegistreringWrapper == null) {
            LOG.info("Bruker ble ikke funnet i databasen.");
        }

        return brukerRegistreringWrapper;
    }

    @POST
    @Path("/startreaktivering")
    @Override
    public void reaktivering() {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));
        inaktivBrukerService.reaktiverBruker(bruker);

        if (AutentiseringUtils.erVeileder()) {
            reportFields(MANUELL_REAKTIVERING_EVENT);
        }

        AlderMetrikker.rapporterAlder(bruker.getGjeldendeFoedselsnummer());
    }

    @POST
    @Path("/startregistrersykmeldt")
    @Override
    public void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering) {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();
        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        NavVeileder veileder = navVeileder();

        sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker, veileder);

        reportFields(SYKMELDT_BESVARELSE_EVENT,
                sykmeldtRegistrering.getBesvarelse().getUtdanning(),
                sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon());
    }

    private NavVeileder navVeileder() {
        if (!AutentiseringUtils.erVeileder()) {
            return null;
        }

        return new NavVeileder(
                AutentiseringUtils.hentIdent()
                        .orElseThrow(() -> new RuntimeException("Fant ikke ident")),
                userService.getEnhetIdFromUrlOrThrow());
    }

    private boolean tjenesteErNede() {
        return unleashService.isEnabled("arbeidssokerregistrering.nedetid");
    }

}