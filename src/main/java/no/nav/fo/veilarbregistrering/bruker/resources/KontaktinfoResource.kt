package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class KontaktinfoResource implements KontaktinfoApi {

    private final KontaktinfoService kontaktinfoService;
    private final UserService userService;
    private final AutorisasjonService autorisasjonService;

    public KontaktinfoResource(
            UserService userService,
            KontaktinfoService kontaktinfoService,
            AutorisasjonService autorisasjonService) {
        this.autorisasjonService = autorisasjonService;
        this.userService = userService;
        this.kontaktinfoService = kontaktinfoService;
    }

    @Override
    @GetMapping("/kontaktinfo")
    public KontaktinfoDto hentKontaktinfo() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        autorisasjonService.sjekkLesetilgangTilBruker(bruker.getGjeldendeFoedselsnummer());

        Kontaktinfo kontaktinfo = kontaktinfoService.hentKontaktinfo(bruker);
        return KontaktinfoMapper.map(kontaktinfo);
    }
}