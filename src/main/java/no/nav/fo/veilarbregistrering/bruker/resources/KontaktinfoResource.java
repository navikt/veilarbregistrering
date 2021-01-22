package no.nav.fo.veilarbregistrering.bruker.resources;

import no.nav.common.abac.Pep;
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
    private final Pep pepClient;

    public KontaktinfoResource(
            Pep pepClient,
            UserService userService,
            KontaktinfoService kontaktinfoService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.kontaktinfoService = kontaktinfoService;
    }

    @Override
    @GetMapping("/kontaktinfo")
    public KontaktinfoDto hentKontaktinfo() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        //TODO pepClient.sjekkLesetilgangTilBruker(map(bruker));

        Kontaktinfo kontaktinfo = kontaktinfoService.hentKontaktinfo(bruker);
        return KontaktinfoMapper.map(kontaktinfo);
    }
}