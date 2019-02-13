package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.PepClient;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.service.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.service.UserService;
import no.nav.fo.veilarbregistrering.utils.AutentiseringUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.*;

@Component
@Path("/")
@Produces("application/json")
@Api(value = "RegistreringResource", description = "Tjenester for registrering og reaktivering av arbeidssøker.")
public class RegistreringResource {

    private final RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature;
    private final RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature;
    private final BrukerRegistreringService brukerRegistreringService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final UserService userService;
    private final ManuellRegistreringService manuellRegistreringService;
    private final PepClient pepClient;

    public RegistreringResource(
            PepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.manuellRegistreringService = manuellRegistreringService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.tjenesteNedeFeature = tjenesteNedeFeature;
        this.manuellRegistreringFeature = manuellRegistreringFeature;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        final String fnr = userService.hentFnrFraUrlEllerToken();

        pepClient.sjekkLeseTilgangTilFnr(fnr);
        StartRegistreringStatus status = brukerRegistreringService.hentStartRegistreringStatus(fnr);
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
        final String fnr = userService.hentFnrFraUrlEllerToken();

        pepClient.sjekkSkriveTilgangTilFnr(fnr);

        if (AutentiseringUtils.erInternBruker()) {

            if (!manuellRegistreringFeature.skalBrukereBliManueltRegistrert()){
                throw new RuntimeException("Bruker kan ikke bli manuelt registrert");
            }

            final String enhetId = manuellRegistreringService.getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, fnr);

            manuellRegistreringService.lagreManuellRegistrering(fnr, veilederIdent, enhetId);

        } else {
            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, fnr);
        }

        rapporterAlder(fnr);

        return registrering;
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
    public BrukerRegistreringWrapper hentRegistrering() {
        final String fnr = userService.hentFnrFraUrlEllerToken();

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

        final String fnr = userService.hentFnrFraUrlEllerToken();

        pepClient.sjekkSkriveTilgangTilFnr(fnr);
        brukerRegistreringService.reaktiverBruker(fnr);
        rapporterAlder(fnr);
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        final String fnr = userService.hentFnrFraUrlEllerToken();

        pepClient.sjekkLeseTilgangTilFnr(fnr);
        return arbeidsforholdService.hentSisteArbeidsforhold(fnr);
    }

    @POST
    @Path("/startregistrersykmeldt")
    @ApiOperation(value = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    public void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering) {

        if(tjenesteNedeFeature.erTjenesteNede()){
            throw new RuntimeException("Tjenesten er nede for øyeblikket. Prøv igjen senere.");
        }


        final String fnr = userService.hentFnrFraUrlEllerToken();
        pepClient.sjekkSkriveTilgangTilFnr(fnr);

        if (AutentiseringUtils.erInternBruker()) {

            if (!manuellRegistreringFeature.skalBrukereBliManueltRegistrert()){
                throw new RuntimeException("Bruker kan ikke bli manuelt registrert");
            }

            final String enhetId = manuellRegistreringService.getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, fnr);

            manuellRegistreringService.lagreManuellRegistrering(fnr, veilederIdent, enhetId);

        } else {
            brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, fnr);
        }

        rapporterSykmeldtBesvarelse(sykmeldtRegistrering);
    }

    @GET
    @Path("/sykmeldtinfodata")
    @ApiOperation(value = "Henter sykmeldt informasjon")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final String fnr = userService.hentFnrFraUrlEllerToken();

        pepClient.sjekkLeseTilgangTilFnr(fnr);
        return brukerRegistreringService.hentSykmeldtInfoData(fnr);
    }

}
