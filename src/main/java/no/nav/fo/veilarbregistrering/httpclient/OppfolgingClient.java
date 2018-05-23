package no.nav.fo.veilarbregistrering.httpclient;

import io.vavr.control.Try;
import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.domain.ArenaOppfolging;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClient {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    private final String baseUrl;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;
    private final Provider<HttpServletRequest> httpServletRequestProvider;

    private static final Logger LOG = LoggerFactory.getLogger(OppfolgingClient.class);


    @Inject
    public OppfolgingClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        this(getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME), new SystemUserAuthorizationInterceptor(), httpServletRequestProvider);
    }

    public OppfolgingClient(String baseUrl, SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor, Provider<HttpServletRequest> httpServletRequestProvider) {
        this.baseUrl = baseUrl;
        this.systemUserAuthorizationInterceptor = systemUserAuthorizationInterceptor;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        try {
            withClient(c -> c.target(baseUrl + "/oppfolging/aktiverbruker")
                    .register(systemUserAuthorizationInterceptor)
                    .request()
                    .post(Entity.json(aktiverBrukerData), AktiverBrukerData.class));
        } catch (Exception e) {
            LOG.error("Feil ved aktivering av bruker mot Oppfølging " + e);
            throw new InternalServerErrorException();
        }
    }

    public Optional<OppfolgingStatus> hentOppfolgingsstatus(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        Optional<OppfolgingStatus> oppfolgingStatus = getOppfolging(baseUrl + "/oppfolging?fnr="+fnr, cookies, OppfolgingStatus.class);

        if (oppfolgingStatus.isPresent()) {
            Optional<ArenaOppfolging> arenaOppfolging = getOppfolging(baseUrl + "/person/" + fnr + "/oppfoelgingsstatus", cookies, ArenaOppfolging.class);
            oppfolgingStatus.get().setInaktiveringsdato(arenaOppfolging.map(a -> a.getInaktiveringsdato()).orElse(null));
        }
        return oppfolgingStatus;
    }

    private static <T> Optional<T> getOppfolging(String url, String cookies, Class<T> returnType) {
        return Try.of(() -> withClient(c -> c.target(url).request().header(COOKIE, cookies).get(returnType)))
                .map(Optional::of)
                .recover(NotFoundException.class, Optional.empty())
                .onFailure((e) -> {
                    LOG.error("Feil ved kall til Oppfølging " + url + ", " + e);
                    throw new InternalServerErrorException();
                })
                .get();

    }
}