package no.nav.fo.veilarbregistrering.config;

import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AAregServiceWSConfig {
    public static final String AAREG_ENDPOINT_URL = "aareg.endpoint.url";
    final static String url = getProperty(AAREG_ENDPOINT_URL);

    public static CXFClient<ArbeidsforholdV3> arbeidsforholdV3CXFClient() {
        return new CXFClient<>(ArbeidsforholdV3.class)
                .withOutInterceptor(new LoggingOutInterceptor())
                .address(url);
    }

    @Bean
    ArbeidsforholdV3 arbeidsforholdV3() {
        return arbeidsforholdV3CXFClient()
                .withOutInterceptor(new UserSAMLOutInterceptor())
                .build();
    }

    @Bean
    Pingable arbeidsforholdPing() {
        final ArbeidsforholdV3 arbeidsforholdV3Ping = arbeidsforholdV3CXFClient()
                .configureStsForSystemUserInFSS()
                .build();

        Pingable.Ping.PingMetadata metadata = new Pingable.Ping.PingMetadata(
                "ArbeidsforholdV3 via " + url,
                "Ping av ArbeidsforholdV3. Henter informasjon om arbeidsforhold fra aareg.",
                false
        );

        return () -> {
            try {
                arbeidsforholdV3Ping.ping();
                return lyktes(metadata);
            } catch (Exception e) {
                return feilet(metadata, e);
            }
        };
    }


}
