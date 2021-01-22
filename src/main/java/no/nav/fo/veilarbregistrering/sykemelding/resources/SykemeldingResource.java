package no.nav.fo.veilarbregistrering.sykemelding.resources;

import no.nav.common.abac.Pep;
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
    private final Pep pepClient;

    public SykemeldingResource(
            Pep pepClient,
            UserService userService,
            SykemeldingService sykemeldingService) {
        this.pepClient = pepClient;
        this.userService = userService;
        this.sykemeldingService = sykemeldingService;
    }

    @Override
    @GetMapping("/sykmeldtinfodata")
    public SykmeldtInfoData hentSykmeldtInfoData() {
        final Bruker bruker = userService.finnBrukerGjennomPdl();

        //pepClient.sjekkLesetilgangTilBruker(BrukerAdapter.map(bruker));

        return sykemeldingService.hentSykmeldtInfoData(bruker.getGjeldendeFoedselsnummer());
    }
}