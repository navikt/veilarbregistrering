package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AAregServiceWSConfig {

    @Bean
    ArbeidsforholdGateway soapArbeidsforholdGateway() {
        return new StubArbeidsforholdGateway();
    }

    @Bean
    ArbeidsforholdGateway restArbeidsforholdGateway() {
        return new RestArbeidsforholdGateway(new StubAaregRestClient());
    }

    @Bean
    ArbeidsforholdGateway proxyArbeidsforholdGatway(
            @Qualifier("soapArbeidsforholdGateway") ArbeidsforholdGateway soapArbeidsforholdGateway,
            @Qualifier("restArbeidsforholdGateway") ArbeidsforholdGateway restArbeidsforholdGateway,
            UnleashService unleashService) {
        return new ProxyArbeidsforholdGateway(soapArbeidsforholdGateway, restArbeidsforholdGateway, unleashService);
    }

    @Bean
    Pingable arbeidsforholdPing() {
        Pingable.Ping.PingMetadata metadata = new Pingable.Ping.PingMetadata(
                "ArbeidsforholdV3 via MOCK",
                "Ping av ArbeidsforholdV3. Henter informasjon om arbeidsforhold fra aareg.",
                false
        );

        return () -> {
            try {
                return lyktes(metadata);
            } catch (Exception e) {
                return feilet(metadata, e);
            }
        };
    }

}
