package no.nav.fo.veilarbregistrering.bruker;

import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.dialogarena.aktor.AktorService;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserService {

    private final Provider<HttpServletRequest> requestProvider;
    private final AktorService aktorService;

    public UserService(Provider<HttpServletRequest> requestProvider, AktorService aktorService) {
        this.requestProvider = requestProvider;
        this.aktorService = aktorService;
    }

    public BrukerIntern hentBrukerIntern() {
        String fnr = hentFnrFraUrlEllerToken();

        String aktorId = aktorService.getAktorId(fnr).orElseThrow(() -> new Feil(FeilType.FINNES_IKKE));

        return BrukerIntern.of(Foedselsnummer.of(fnr), AktorId.valueOf(aktorId));
    }

    public Bruker hentBruker() {
        String fnr = hentFnrFraUrlEllerToken();

        return Bruker.fraFnr(fnr)
                .medAktoerIdSupplier(()->aktorService.getAktorId(fnr).orElseThrow(()->new Feil(FeilType.FINNES_IKKE)));
    }

    private String hentFnrFraUrlEllerToken() {

        String fnr = getFnrFromUrl();

        if (fnr == null) {
            fnr = getFnr();
        }

        if (!isValid(fnr)) {
            throw new RuntimeException("Fødselsnummer ikke gyldig.");
        }

        return fnr;
    }

    public String getFnrFromUrl() {
        return requestProvider.get().getParameter("fnr");
    }

    public String getFnr() {
        return getIdent().orElseThrow(IllegalArgumentException::new);
    }

    public String getEnhetIdFromUrlOrThrow() {
        final String enhetId = requestProvider.get().getParameter("enhetId");

        if (enhetId == null) {
            throw new RuntimeException("Mangler enhetId");
        }

        return enhetId;
    }
}
