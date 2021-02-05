package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdMapper.map;

@RestController
@RequestMapping("/api")
public class ArbeidsforholdResource implements ArbeidsforholdApi {

    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final UserService userService;
    private final AutorisasjonService autorisasjonService;

    @Override
    @GetMapping("/sistearbeidsforhold")
    public ArbeidsforholdDto hentSisteArbeidsforhold() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        autorisasjonService.sjekkLesetilgangTilBruker(bruker.getGjeldendeFoedselsnummer());

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(bruker.getGjeldendeFoedselsnummer());
        return map(flereArbeidsforhold.siste());
    }

    public ArbeidsforholdResource(
            AutorisasjonService autorisasjonService,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway) {
        this.autorisasjonService = autorisasjonService;
        this.userService = userService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
    }
}