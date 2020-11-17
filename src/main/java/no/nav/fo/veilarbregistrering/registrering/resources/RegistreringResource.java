package no.nav.fo.veilarbregistrering.registrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.besvarelse.Stilling;
import no.nav.fo.veilarbregistrering.bruker.AutentiseringUtils;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.*;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportFields;
import static no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType.ORDINAER;
import static no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType.SYKMELDT;
import static no.nav.fo.veilarbregistrering.registrering.resources.StartRegistreringStatusMetrikker.rapporterRegistreringsstatus;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "RegistreringResource", description = "Tjenester for registrering og reaktivering av arbeidssøker.")
public class RegistreringResource {

    private static final Logger LOG = LoggerFactory.getLogger(RegistreringResource.class);

    private final UnleashService unleashService;
    private final BrukerRegistreringService brukerRegistreringService;
    private final HentRegistreringService hentRegistreringService;
    private final UserService userService;
    private final ManuellRegistreringService manuellRegistreringService;
    private final VeilarbAbacPepClient pepClient;

    public RegistreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            BrukerRegistreringService brukerRegistreringService,
            HentRegistreringService hentRegistreringService, UnleashService unleashService
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.manuellRegistreringService = manuellRegistreringService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.hentRegistreringService = hentRegistreringService;
        this.unleashService = unleashService;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatusDto hentStartRegistreringStatus() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkLesetilgangTilBruker(map(bruker)); //FIXME: BrukerAdapter bør i stedet være pepClient-adapter
        StartRegistreringStatusDto status = brukerRegistreringService.hentStartRegistreringStatus(bruker);
        rapporterRegistreringsstatus(status);
        return status;
    }

    @POST
    @Path("/startregistrering")
    @ApiOperation(value = "Starter nyregistrering av arbeidssøker.")
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        OrdinaerBrukerRegistrering registrering;
        if (AutentiseringUtils.erVeileder()) {

            if (!brukereSkalBliManueltRegistrert()){
                throw new RuntimeException("Bruker kan ikke bli manuelt registrert");
            }

            final String enhetId = userService.getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker);
            manuellRegistreringService.lagreManuellRegistrering(veilederIdent, enhetId, registrering.getId(), ORDINAER);

            reportFields(MANUELL_REGISTRERING_EVENT, ORDINAER);

        } else {
            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker);
        }

        AlderMetrikker.rapporterAlder(bruker.getGjeldendeFoedselsnummer());

        return registrering;
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
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

    @GET
    @Path("/dummyregistrering")
    @ApiOperation(value = "Henter dummy registrering.")
    public OrdinaerBrukerRegistrering hentDummyRegistrering() {

        Stilling stilling = new Stilling();
        stilling.setLabel("Barnehageassistent");

        TekstForSporsmal tekstForSporsmal1 = new TekstForSporsmal("", "Hva tenker du om din fremtidige situasjon?", "Jeg trenger ny jobb");
        TekstForSporsmal tekstForSporsmal2 = new TekstForSporsmal("", "Er utdanningen din bestått?", "Ikke aktuelt");
        TekstForSporsmal tekstForSporsmal3 = new TekstForSporsmal("", "Er utdanningen din godkjent i Norge?", "Ikke aktuelt");
        TekstForSporsmal tekstForSporsmal4 = new TekstForSporsmal("", "Hva er din høyeste fullførte utdanning?", "Ingen utdanning");
        TekstForSporsmal tekstForSporsmal5 = new TekstForSporsmal("", "Er det noe annet enn helsen din som NAV bør ta hensyn til?", "Nei");
        List<TekstForSporsmal> teksterForSporsmal = Arrays.asList(tekstForSporsmal1, tekstForSporsmal2, tekstForSporsmal3, tekstForSporsmal4, tekstForSporsmal5);

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = new OrdinaerBrukerRegistrering();
        ordinaerBrukerRegistrering.setId(103);
        ordinaerBrukerRegistrering.setOpprettetDato(LocalDateTime.now());
        ordinaerBrukerRegistrering.setSisteStilling(stilling);
        ordinaerBrukerRegistrering.setTeksterForBesvarelse(teksterForSporsmal);

        return ordinaerBrukerRegistrering;
    }

    @POST
    @Path("/startreaktivering")
    @ApiOperation(value = "Starter reaktivering av arbeidssøker.")
    public void reaktivering() {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();

        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));
        brukerRegistreringService.reaktiverBruker(bruker);

        if (AutentiseringUtils.erVeileder()) {
            reportFields(MANUELL_REAKTIVERING_EVENT);
        }

        AlderMetrikker.rapporterAlder(bruker.getGjeldendeFoedselsnummer());
    }

    @POST
    @Path("/startregistrersykmeldt")
    @ApiOperation(value = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    public void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering) {

        if(tjenesteErNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = userService.finnBrukerGjennomPdl();
        pepClient.sjekkSkrivetilgangTilBruker(map(bruker));

        if (AutentiseringUtils.erVeileder()) {

            if (!brukereSkalBliManueltRegistrert()){
                throw new RuntimeException("Bruker kan ikke bli manuelt registrert");
            }

            final String enhetId = userService.getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            long id = brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker);
            manuellRegistreringService.lagreManuellRegistrering(veilederIdent, enhetId, id, SYKMELDT);

            reportFields(MANUELL_REGISTRERING_EVENT, SYKMELDT);

        } else {
            brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker);
        }

        reportFields(SYKMELDT_BESVARELSE_EVENT,
                sykmeldtRegistrering.getBesvarelse().getUtdanning(),
                sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon());
    }

    private boolean brukereSkalBliManueltRegistrert() {
        return unleashService.isEnabled("arbeidssokerregistrering.manuell_registrering");
    }

    private boolean tjenesteErNede() {
        return unleashService.isEnabled("arbeidssokerregistrering.nedetid");
    }

}