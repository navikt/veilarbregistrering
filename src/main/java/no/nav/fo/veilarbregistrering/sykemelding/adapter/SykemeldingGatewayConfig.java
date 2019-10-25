package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class SykemeldingGatewayConfig {

    public static final String INFOTRYGDAPI_URL_PROPERTY_NAME = "http://infotrygd-fo.default.svc.nais.local";

    @Value(INFOTRYGDAPI_URL_PROPERTY_NAME)
    private String baseUrlInfotrygdApi;

    @Bean
    SykmeldtInfoClient sykeforloepMetadataClient(Provider<HttpServletRequest> provider) {
        return new SykmeldtInfoClient(baseUrlInfotrygdApi, provider);
    }

    @Bean
    SykemeldingGateway sykemeldingGateway(SykmeldtInfoClient sykeforloepMetadataClient) {
        return new SykemeldingGatewayImpl(sykeforloepMetadataClient);
    }
}
