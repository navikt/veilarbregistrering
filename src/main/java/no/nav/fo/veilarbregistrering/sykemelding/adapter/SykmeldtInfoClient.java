package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.common.oidc.utils.TokenUtils;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;

public class SykmeldtInfoClient extends BaseClient {

    public SykmeldtInfoClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        super(baseUrl, httpServletRequestProvider);
    }

    public InfotrygdData hentSykmeldtInfoData(Foedselsnummer fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr.stringValue());
    }

    private InfotrygdData getSykeforloepMetadata(String url) {
        HttpServletRequest request = httpServletRequestProvider.get();

        try {
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, request.getHeader(COOKIE))
                            .header("Authorization", "Bearer " + TokenUtils.getTokenFromHeader(request).orElse(null))
                            .get(InfotrygdData.class));
        } catch (Exception e) {
            throw new InternalServerErrorException("Hent maksdato fra Infotrygd feilet.", e);
        }
    }
}
