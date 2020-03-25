package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OppgaveGatewayConfig {

    public static final String OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL";

    @Bean
    OppgaveGateway oppgaveGateway() {

        return new OppgaveGateway() {

            @Override
            public Oppgave opprettOppgave(AktorId aktoerId, String tilordnetRessurs, String beskrivelse) {
                return null;
            }

            @Override
            public Oppgave opprettOppgaveArbeidstillatelse(AktorId aktoerId, String beskrivelse) {
                return null;
            }
        };
    }
}
