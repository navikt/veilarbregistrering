package no.nav.fo.veilarbregistrering.bruker;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;
import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserService {

    private final Provider<HttpServletRequest> requestProvider;
    private final AktorGateway aktorGateway;
    private final PdlOppslagGateway pdlOppslagGateway;

    public UserService(Provider<HttpServletRequest> requestProvider, AktorGateway aktorGateway, PdlOppslagGateway pdlOppslagGateway) {
        this.requestProvider = requestProvider;
        this.aktorGateway = aktorGateway;
        this.pdlOppslagGateway = pdlOppslagGateway;
    }

    public Bruker hentBrukerFra(Kilde kilde) {
        if (kilde.equals(Kilde.PDL)) {
            return finnBrukerGjennomPdl();

        } else if (kilde.equals(Kilde.AKTOR)) {
            return hentBruker();

        } else {
            throw new IllegalArgumentException(format("hentBruker ble kalt med ukjent kilde, %s", kilde));
        }
    }

    public Bruker finnBrukerGjennomPdl() {
        Foedselsnummer fnr = hentFnrFraUrlEllerToken();
        return finnBrukerGjennomPdl(fnr);
    }

    public Bruker finnBrukerGjennomPdl(Foedselsnummer fnr) {
        Identer identer = pdlOppslagGateway.hentIdenter(fnr);
        return Bruker.of(
                identer.finnGjeldendeFnr(),
                identer.finnGjeldendeAktorId(),
                identer.finnHistoriskeFoedselsnummer());
    }

    private Bruker hentBruker() {
        Foedselsnummer fnr = hentFnrFraUrlEllerToken();

        return hentBruker(fnr);
    }

    public Bruker hentBruker(Foedselsnummer fnr) {
        AktorId aktorId = aktorGateway.hentAktorIdFor(fnr);

        return Bruker.of(fnr, aktorId);
    }

    public Bruker hentBruker(AktorId aktorId) {
        Foedselsnummer fnr = aktorGateway.hentFnrFor(aktorId);

        return Bruker.of(fnr, aktorId);
    }

    public Foedselsnummer hentFnrFraUrlEllerToken() {

        String fnr = getFnrFromUrl();

        if (fnr == null) {
            fnr = getFnr();
        }

        if (!isValid(fnr)) {
            throw new RuntimeException("Fødselsnummer ikke gyldig.");
        }

        return Foedselsnummer.of(fnr);
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

    public enum Kilde {
        PDL,
        AKTOR
    }
}
