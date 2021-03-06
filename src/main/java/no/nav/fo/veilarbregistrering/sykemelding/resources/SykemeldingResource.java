package no.nav.fo.veilarbregistrering.sykemelding.resources;

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SykemeldingResource implements SykemeldingApi {

    private final SykemeldingService sykemeldingService;
    private final UserService userService;
    private final AutorisasjonService autorisasjonsService;

    public SykemeldingResource(
            UserService userService,
            SykemeldingService sykemeldingService,
            AutorisasjonService autorisasjonsService) {
        this.autorisasjonsService = autorisasjonsService;
        this.userService = userService;
        this.sykemeldingService = sykemeldingService;
    }

    @Override
    @GetMapping("/sykmeldtinfodata")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        autorisasjonsService.sjekkLesetilgangTilBruker(bruker.getGjeldendeFoedselsnummer());

        return sykemeldingService.hentSykmeldtInfoData(bruker.getGjeldendeFoedselsnummer());
    }
}