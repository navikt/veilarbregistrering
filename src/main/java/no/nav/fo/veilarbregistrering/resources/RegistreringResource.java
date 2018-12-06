package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.PepClient;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.service.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.*;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "RegistreringResource", description = "Tjenester for registrering og reaktivering av arbeidssøker.")
public class RegistreringResource {

    private final RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature;
    private BrukerRegistreringService brukerRegistreringService;
    private ArbeidsforholdService arbeidsforholdService;
    private UserService userService;
    private PepClient pepClient;

    public RegistreringResource(
            PepClient pepClient,
            UserService userService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.tjenesteNedeFeature = tjenesteNedeFeature;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        StartRegistreringStatus status = brukerRegistreringService.hentStartRegistreringStatus(userService.getFnr());
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

        pepClient.sjekkSkriveTilgangTilFnr(userService.getFnr());
        OrdinaerBrukerRegistrering registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, userService.getFnr());
        rapporterAlder(userService.getFnr());
        return registrering;
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
    public BrukerRegistreringWrapper hentRegistrering() {
        String fnr = userService.getFnrFromUrl();

        if(fnr == null){
            fnr = userService.getFnr();
        }

        if (!isValid(fnr)) {
            throw new RuntimeException("Fødselsnummer ikke gyldig.");
        }

        pepClient.sjekkLeseTilgangTilFnr(fnr);
        return brukerRegistreringService.hentBrukerRegistrering(new Fnr(fnr));
    }

    @POST
    @Path("/startreaktivering")
    @ApiOperation(value = "Starter reaktivering av arbeidssøker.")
    public void reaktivering() {

        if(tjenesteNedeFeature.erTjenesteNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        pepClient.sjekkSkriveTilgangTilFnr(userService.getFnr());
        brukerRegistreringService.reaktiverBruker(userService.getFnr());
        rapporterAlder(userService.getFnr());
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return arbeidsforholdService.hentSisteArbeidsforhold(userService.getFnr());
    }

    @POST
    @Path("/startregistrersykmeldt")
    @ApiOperation(value = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    public void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering) {

        if(tjenesteNedeFeature.erTjenesteNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }

        pepClient.sjekkSkriveTilgangTilFnr(userService.getFnr());
        brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, userService.getFnr());
        rapporterSykmeldtBesvarelse(sykmeldtRegistrering);
    }

    @GET
    @Path("/sykmeldtinfodata")
    @ApiOperation(value = "Henter sykmeldt informasjon")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        pepClient.sjekkLeseTilgangTilFnr(userService.getFnr());
        return brukerRegistreringService.hentSykmeldtInfoData(userService.getFnr());
    }
}
