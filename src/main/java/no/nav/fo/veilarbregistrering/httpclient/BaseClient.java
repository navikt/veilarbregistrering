package no.nav.fo.veilarbregistrering.httpclient;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Slf4j
public class BaseClient {

    protected final String baseUrl;
    protected final Provider<HttpServletRequest> httpServletRequestProvider;
    protected static final int HTTP_READ_TIMEOUT = 120000;

    public BaseClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        this.baseUrl = baseUrl;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

    protected int behandleHttpResponse(Response response, String url) {
        int status = response.getStatus();

        if (status == 204) {
            return status;
        } else if (status == 403) {
            log.error("Feil ved kall mot : {}, response : {}", url, response);
            throw new WebApplicationException(response);
        } else {
            throw new RuntimeException("Uventet respons (" + status + ") ved kall mot mot " + url);
        }
    }

}
