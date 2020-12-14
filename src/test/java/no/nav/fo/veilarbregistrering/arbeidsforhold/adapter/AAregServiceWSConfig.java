package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.sbl.dialogarena.types.Pingable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AAregServiceWSConfig {

    @Bean
    ArbeidsforholdGateway arbeidsforholdGateway() {
        return new StubArbeidsforholdGateway();
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
