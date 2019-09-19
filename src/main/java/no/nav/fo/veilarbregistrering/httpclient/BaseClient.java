package no.nav.fo.veilarbregistrering.httpclient;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class BaseClient {

    protected final String baseUrl;
    protected final Provider<HttpServletRequest> httpServletRequestProvider;
    protected static final int HTTP_READ_TIMEOUT = 120000;

    public BaseClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        this.baseUrl = baseUrl;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

}
