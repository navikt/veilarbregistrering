package no.nav.fo.veilarbregistrering.bruker;

import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.bekk.bekkopen.person.FodselsnummerValidator.isValid;
import static no.nav.common.auth.SubjectHandler.getIdent;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final Provider<HttpServletRequest> requestProvider;
    private final AktorGateway aktorGateway;
    private final PdlOppslagGateway pdlOppslagGateway;

    public UserService(Provider<HttpServletRequest> requestProvider, AktorGateway aktorGateway, PdlOppslagGateway pdlOppslagGateway) {
        this.requestProvider = requestProvider;
        this.aktorGateway = aktorGateway;
        this.pdlOppslagGateway = pdlOppslagGateway;
    }

    public Bruker hentBruker() {
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

    public Bruker finnBrukerGjennomPdl(Foedselsnummer fnr) {
        try {
            return pdlOppslagGateway.hentIdenter(fnr)
                    .map(identer -> Bruker.of(identer.finnGjeldendeFnr(), identer.finnGjeldendeAktorId(), identer.finnHistoriskeFoedselsnummer()))
                    .orElse(null);
        } catch (RuntimeException e) {
            LOG.error("Hent identer fra PDL feilet", e);
            throw new Feil(FeilType.UKJENT);
        }

    }
}
