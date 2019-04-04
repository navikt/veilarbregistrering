package no.nav.fo.veilarbregistrering.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.apiapp.security.PepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.domain.AktorId;
import no.nav.fo.veilarbregistrering.domain.FremtidigSituasjonData;
import no.nav.fo.veilarbregistrering.service.*;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.getAktorIdOrElseThrow;


@Component
@Path("/infoommeg")
@Produces("application/json")
@Api(value = "InfoOmMegResource", description = "Tjenester for deling av arbeidssøkerstatus.")

public class InfoOmMegResource {
    private final InfoOmMegService infoOmMegService;
    private final UserService userService;
    private final AktorService aktorService;
    private final PepClient pepClient;

    public InfoOmMegResource(
            InfoOmMegService infoOmMegService,
            UserService userService,
            AktorService aktorService,
            PepClient pepClient
    ) {

        this.infoOmMegService = infoOmMegService;
        this.userService = userService;
        this.aktorService = aktorService;
        this.pepClient = pepClient;
    }

    @GET
    @Path("/fremtidigsituasjon")
    @ApiOperation(value = "Henter nyeste verdi for fremtidig situasjon.")
    public FremtidigSituasjonData hentFremtidigSituasjon() {
        String fnr = userService.hentFnrFraUrlEllerToken();
        AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);

        pepClient.sjekkLeseTilgangTilFnr(fnr);
        return infoOmMegService.hentFremtidigSituasjon(aktorId);
    }

    @POST
    @Path("/fremtidigsituasjon")
    @ApiOperation(value = "Oppdaterer fremtidig situasjon")
    public void oppdaterFremtidigSituasjon(FremtidigSituasjonData fremtidigSituasjonData) {
        String fnr = userService.hentFnrFraUrlEllerToken();
        AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);
        String endretAv = userService.erEksternBruker()? aktorId.getAktorId() : userService.getUid();

        pepClient.sjekkLeseTilgangTilFnr(fnr);
        infoOmMegService.lagreFremtidigSituasjon(fremtidigSituasjonData, aktorId, endretAv);
    }

}
