package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Configuration
@Import(SykmeldtInfoClientHelseSjekk.class)
public class SykemeldingGatewayConfig {

    //FIXME: Verdien skal ikke være hardkodet, men hentes via properties
    public static final String INFOTRYGDAPI_URL_PROPERTY_NAME = "http://infotrygd-fo.default.svc.nais.local";

    @Bean
    SykmeldtInfoClient sykeforloepMetadataClient(Provider<HttpServletRequest> provider) {
        return new SykmeldtInfoClient(INFOTRYGDAPI_URL_PROPERTY_NAME, provider);
    }

    @Bean
    SykemeldingGateway sykemeldingGateway(SykmeldtInfoClient sykeforloepMetadataClient) {
        return new SykemeldingGatewayImpl(sykeforloepMetadataClient);
    }
}
