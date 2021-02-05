package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class SykemeldingGatewayConfig {

    //FIXME: Verdien skal ikke være hardkodet, men hentes via properties
    public static final String INFOTRYGDAPI_URL_PROPERTY_NAME = "http://infotrygd-fo.default.svc.nais.local";

    @Bean
    SykmeldtInfoClient sykeforloepMetadataClient() {
        return new SykmeldtInfoClient(INFOTRYGDAPI_URL_PROPERTY_NAME);
    }

    @Bean
    SykemeldingGateway sykemeldingGateway(SykmeldtInfoClient sykeforloepMetadataClient) {
        return new SykemeldingGatewayImpl(sykeforloepMetadataClient);
    }
}
