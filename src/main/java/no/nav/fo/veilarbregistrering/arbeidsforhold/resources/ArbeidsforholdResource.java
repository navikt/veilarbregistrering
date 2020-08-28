package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdMapper.map;
import static no.nav.fo.veilarbregistrering.bruker.BrukerAdapter.map;

@Component
@Path("/")
@Produces("application/json")
public class ArbeidsforholdResource implements ArbeidsforholdApi {

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final UserService userService;
    private final VeilarbAbacPepClient pepClient;
    private final UnleashService unleashService;

    public ArbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway,
            UnleashService unleashService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.unleashService = unleashService;
    }

    @GET
    @Path("/sistearbeidsforhold")
    @Override
    public ArbeidsforholdDto hentSisteArbeidsforhold() {
        final Bruker bruker = userService.hentBruker(velgKilde());

        pepClient.sjekkLesetilgangTilBruker(map(bruker));

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer());
        return map(flereArbeidsforhold.siste());
    }

    private UserService.Kilde velgKilde() {
        return unleashService.isEnabled("veilarbregistrering.arbeidsforhold.kildePdl") ? UserService.Kilde.PDL : UserService.Kilde.AKTOR;
    }
}
