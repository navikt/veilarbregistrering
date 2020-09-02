package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;

@Component
@Path("/person")
@Produces("application/json")
public class KontaktinfoResource implements KontaktinfoApi {

    private final KontaktinfoService kontaktinfoService;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final UnleashService unleashService;

    public KontaktinfoResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            KontaktinfoService kontaktinfoService,
            UnleashService unleashService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.kontaktinfoService = kontaktinfoService;
        this.unleashService = unleashService;
    }

    @GET
    @Path("/kontaktinfo")
    @Override
    public KontaktinfoDto hentKontaktinfo() {
        final Bruker bruker = userService.hentBrukerFra(velgKilde());

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        Kontaktinfo kontaktinfo = kontaktinfoService.hentKontaktinfo(bruker);
        return KontaktinfoMapper.map(kontaktinfo);
    }

    private UserService.Kilde velgKilde() {
        return unleashService.isEnabled("veilarbregistrering.bruker.kildePdl") ? UserService.Kilde.PDL : UserService.Kilde.AKTOR;
    }
}