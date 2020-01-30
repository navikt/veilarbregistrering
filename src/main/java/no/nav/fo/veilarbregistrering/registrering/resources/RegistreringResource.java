package no.nav.fo.veilarbregistrering.registrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.bruker.AutentiseringUtils;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringWrapper;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;

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

    private final RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature;
    private final RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature;
    private final Provider<HttpServletRequest> requestProvider;
    private final BrukerRegistreringService brukerRegistreringService;
    private final UserService userService;
    private final ManuellRegistreringService manuellRegistreringService;
    private final VeilarbAbacPepClient pepClient;
    private final AktorService aktorService;

    public RegistreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature,
            Provider<HttpServletRequest> requestProvider
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.manuellRegistreringService = manuellRegistreringService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.aktorService=aktorService;
        this.tjenesteNedeFeature = tjenesteNedeFeature;
        this.manuellRegistreringFeature = manuellRegistreringFeature;
        this.requestProvider = requestProvider;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatusDto hentStartRegistreringStatus() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        StartRegistreringStatusDto status = brukerRegistreringService.hentStartRegistreringStatus(bruker.getFoedselsnummer());
        rapporterRegistreringsstatus(status);
        return status;
    }

    @POST
    @Path("/startregistrering")
    @ApiOperation(value = "Starter nyregistrering av arbeidssøker.")
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {

        if(tjenesteNedeFeature.erTjenesteNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        OrdinaerBrukerRegistrering registrering;
        final Bruker bruker = hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(bruker);

        if (AutentiseringUtils.erVeileder()) {

            if (!manuellRegistreringFeature.skalBrukereBliManueltRegistrert()){
                throw new RuntimeException("Bruker kan ikke bli manuelt registrert");
            }

            final String enhetId = getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker);
            manuellRegistreringService.lagreManuellRegistrering(veilederIdent, enhetId, registrering.getId(), ORDINAER);

            reportFields(MANUELL_REGISTRERING_EVENT, ORDINAER);

        } else {
            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker);
        }

        AlderMetrikker.rapporterAlder(bruker.getFoedselsnummer());

        return registrering;
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
    public BrukerRegistreringWrapper hentRegistrering() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);

        BrukerRegistreringWrapper brukerRegistreringWrapper = brukerRegistreringService.hentBrukerRegistrering(bruker);
        if (brukerRegistreringWrapper == null) {
            LOG.info("Bruker ble ikke funnet i databasen.");
        }

        return brukerRegistreringWrapper;
    }

    @POST
    @Path("/startreaktivering")
    @ApiOperation(value = "Starter reaktivering av arbeidssøker.")
    public void reaktivering() {

        if(tjenesteNedeFeature.erTjenesteNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = hentBruker();

        pepClient.sjekkSkrivetilgangTilBruker(bruker);
        brukerRegistreringService.reaktiverBruker(bruker);

        if (AutentiseringUtils.erVeileder()) {
            reportFields(MANUELL_REAKTIVERING_EVENT);
        }

        AlderMetrikker.rapporterAlder(bruker.getFoedselsnummer());
    }

    @POST
    @Path("/startregistrersykmeldt")
    @ApiOperation(value = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    public void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering) {

        if(tjenesteNedeFeature.erTjenesteNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        final Bruker bruker = hentBruker();
        pepClient.sjekkSkrivetilgangTilBruker(bruker);

        if (AutentiseringUtils.erVeileder()) {

            if (!manuellRegistreringFeature.skalBrukereBliManueltRegistrert()){
                throw new RuntimeException("Bruker kan ikke bli manuelt registrert");
            }

            final String enhetId = getEnhetIdFromUrlOrThrow();
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

    private Bruker hentBruker() {
        String fnr = userService.hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(()->aktorService.getAktorId(fnr).orElseThrow(()->new Feil(FeilType.FINNES_IKKE)));
    }

    String getEnhetIdFromUrlOrThrow() {
        final String enhetId = requestProvider.get().getParameter("enhetId");

        if (enhetId == null) {
            throw new RuntimeException("Mangler enhetId");
        }

        return enhetId;
    }

}
