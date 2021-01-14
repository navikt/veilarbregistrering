package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdMapper.map;

@Component
@Path("/")
@Produces("application/json")
public class ArbeidsforholdResource implements ArbeidsforholdApi {

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final UserService userService;
    private final AutorisasjonService autorisasjonService;

    public ArbeidsforholdResource(
            AutorisasjonService autorisasjonService,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway) {
        this.autorisasjonService = autorisasjonService;
        this.userService = userService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
    }

    @GET
    @Path("/sistearbeidsforhold")
    @Override
    public ArbeidsforholdDto hentSisteArbeidsforhold() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        autorisasjonService.sjekkLesetilgangTilBruker(bruker.getGjeldendeFoedselsnummer().stringValue());

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer());
        return map(flereArbeidsforhold.siste());
    }
}