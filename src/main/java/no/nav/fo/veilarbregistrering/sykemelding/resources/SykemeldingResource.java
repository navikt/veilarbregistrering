package no.nav.fo.veilarbregistrering.sykemelding.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.BrukerAdapter;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/")
@Produces("application/json")
public class SykemeldingResource implements SykemeldingApi {

    private final SykemeldingService sykemeldingService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final UnleashService unleashService;

    public SykemeldingResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            SykemeldingService sykemeldingService,
            UnleashService unleashService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.sykemeldingService = sykemeldingService;
        this.unleashService = unleashService;
    }

    @GET
    @Path("/sykmeldtinfodata")
    @Override
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final Bruker bruker = userService.hentBrukerFra(velgKilde());

        pepClient.sjekkLesetilgangTilBruker(BrukerAdapter.map(bruker));

        return sykemeldingService.hentSykmeldtInfoData(bruker.getGjeldendeFoedselsnummer());
    }

    private UserService.Kilde velgKilde() {
        return unleashService.isEnabled("veilarbregistrering.sykemelding.kildePdl") ? UserService.Kilde.PDL : UserService.Kilde.AKTOR;
    }
}
