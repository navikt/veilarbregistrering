package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.brukerdialog.security.jaspic.TokenLocator;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.brukerdialog.security.Constants.AZUREADB2C_OIDC_COOKIE_NAME_SBS;
import static no.nav.sbl.rest.RestUtils.withClient;

public class SykmeldtInfoClient extends BaseClient {

    private static final Logger LOG = LoggerFactory.getLogger(SykmeldtInfoClient.class);

    public SykmeldtInfoClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        super(baseUrl, httpServletRequestProvider);
    }

    public InfotrygdData hentSykmeldtInfoData(String fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr);
    }

    private InfotrygdData getSykeforloepMetadata(String url) {
        HttpServletRequest request = httpServletRequestProvider.get();
        TokenLocator tokenLocator = new TokenLocator(AZUREADB2C_OIDC_COOKIE_NAME_SBS, null);

        try {
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, request.getHeader(COOKIE))
                            .header("Authorization", "Bearer " + tokenLocator.getToken(request).orElse(null))
                            .get(InfotrygdData.class));
        } catch (Exception e) {
            throw new InternalServerErrorException("Hent maksdato fra Infotrygd feilet.", e);
        }
    }
}
