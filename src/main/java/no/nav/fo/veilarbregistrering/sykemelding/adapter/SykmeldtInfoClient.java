package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import lombok.extern.slf4j.Slf4j;
import no.nav.brukerdialog.security.jaspic.TokenLocator;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.brukerdialog.security.Constants.AZUREADB2C_OIDC_COOKIE_NAME_SBS;
import static no.nav.sbl.rest.RestUtils.withClient;

@Slf4j
public class SykmeldtInfoClient extends BaseClient {

    public static final String INFOTRYGDAPI_URL_PROPERTY_NAME = "http://infotrygd-fo.default.svc.nais.local";

    @Inject
    public SykmeldtInfoClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(INFOTRYGDAPI_URL_PROPERTY_NAME, httpServletRequestProvider);
    }

    public InfotrygdData hentSykmeldtInfoData(String fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr);
    }

    private InfotrygdData getSykeforloepMetadata(String url) {
        HttpServletRequest request = httpServletRequestProvider.get();
        TokenLocator tokenLocator = new TokenLocator(AZUREADB2C_OIDC_COOKIE_NAME_SBS, null);

        try {
            log.info("Kaller infotrygd-sykepenger på url : " + url);
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, request.getHeader(COOKIE))
                            .header("Authorization", "Bearer " + tokenLocator.getToken(request).orElse(null))
                            .get(InfotrygdData.class));
        } catch (Exception e) {
            log.error("Feil ved kall til tjeneste " + e);
            throw new InternalServerErrorException();
        }
    }
}
