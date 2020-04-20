package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.common.oidc.Constants;
import no.nav.common.oidc.utils.TokenLocator;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class SykmeldtInfoClient extends BaseClient {

    public SykmeldtInfoClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        super(baseUrl, httpServletRequestProvider);
    }

    public InfotrygdData hentSykmeldtInfoData(Foedselsnummer fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr.stringValue());
    }

    private InfotrygdData getSykeforloepMetadata(String url) {
        HttpServletRequest request = httpServletRequestProvider.get();

        // TODO: Flytt ut til konfig
        TokenLocator tokenLocator = new TokenLocator(getRequiredProperty(Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME), null);

        try {
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, request.getHeader(COOKIE))
                            .header("Authorization", "Bearer " + tokenLocator.getIdToken(request).orElse(null))
                            .get(InfotrygdData.class));
        } catch (Exception e) {
            throw new InternalServerErrorException("Hent maksdato fra Infotrygd feilet.", e);
        }
    }
}
