package no.nav.fo.veilarbregistrering.httpclient;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

public class BaseClient {

    protected final String baseUrl;
    protected final Provider<HttpServletRequest> httpServletRequestProvider;
    protected static final int HTTP_READ_TIMEOUT = 120000;

    public BaseClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        this.baseUrl = baseUrl;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

}
