package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
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
    private final VeilarbAbacPepClient pepClient;
    private final AktorService aktorService;

    public RegistreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature
    ) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.manuellRegistreringService = manuellRegistreringService;
        this.brukerRegistreringService = brukerRegistreringService;
        this.aktorService=aktorService;
        this.tjenesteNedeFeature = tjenesteNedeFeature;
        this.manuellRegistreringFeature = manuellRegistreringFeature;
    }

    @GET
    @Path("/startregistrering")
    @ApiOperation(value = "Henter oppfølgingsinformasjon om arbeidssøker.")
    public StartRegistreringStatus hentStartRegistreringStatus() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        StartRegistreringStatus status = brukerRegistreringService.hentStartRegistreringStatus(bruker.getFoedselsnummer());
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

            final String enhetId = manuellRegistreringService.getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker.getFoedselsnummer());

            manuellRegistreringService.lagreManuellRegistrering(veilederIdent, enhetId,
                    registrering.getId(), BrukerRegistreringType.ORDINAER);

            rapporterManuellRegistrering(BrukerRegistreringType.ORDINAER);

        } else {
            registrering = brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker.getFoedselsnummer());
        }

        rapporterAlder(bruker.getFoedselsnummer());

        return registrering;
    }

    @GET
    @Path("/registrering")
    @ApiOperation(value = "Henter siste registrering av bruker.")
    public BrukerRegistreringWrapper hentRegistrering() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        return brukerRegistreringService.hentBrukerRegistrering(new Fnr(bruker.getFoedselsnummer()));
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
        brukerRegistreringService.reaktiverBruker(bruker.getFoedselsnummer());

        if (AutentiseringUtils.erVeileder()) {
            rapporterManuellReaktivering();
        }

        rapporterAlder(bruker.getFoedselsnummer());
    }

    @GET
    @Path("/sistearbeidsforhold")
    @ApiOperation(value = "Henter informasjon om brukers siste arbeidsforhold.")
    public Arbeidsforhold hentSisteArbeidsforhold() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        return arbeidsforholdService.hentSisteArbeidsforhold(bruker.getFoedselsnummer());
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

            final String enhetId = manuellRegistreringService.getEnhetIdFromUrlOrThrow();
            final String veilederIdent = AutentiseringUtils.hentIdent()
                    .orElseThrow(() -> new RuntimeException("Fant ikke ident"));

            long id = brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker.getFoedselsnummer());
            manuellRegistreringService.lagreManuellRegistrering(veilederIdent, enhetId, id, BrukerRegistreringType.SYKMELDT);
            rapporterManuellRegistrering(BrukerRegistreringType.SYKMELDT);

        } else {
            brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, bruker.getFoedselsnummer());
        }

        rapporterSykmeldtBesvarelse(sykmeldtRegistrering);
    }

    @GET
    @Path("/sykmeldtinfodata")
    @ApiOperation(value = "Henter sykmeldt informasjon")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final Bruker bruker = hentBruker();

        pepClient.sjekkLesetilgangTilBruker(bruker);
        return brukerRegistreringService.hentSykmeldtInfoData(bruker.getFoedselsnummer());
    }

    private Bruker hentBruker() {
        String fnr = userService.hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(()->aktorService.getAktorId(fnr).orElseThrow(()->new Feil(FeilType.FINNES_IKKE)));
    }



}
