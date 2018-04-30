package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.PepClient;
import no.nav.sbl.dialogarena.common.abac.pep.Pep;
import no.nav.sbl.dialogarena.common.abac.pep.context.AbacContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static no.nav.sbl.dialogarena.common.abac.pep.domain.ResourceType.Person;

@Configuration
@Import({AbacContext.class})
public class PepConfig {

    public static final String DOMAIN_VEILARB = "veilarb";

    @Bean
    public PepClient pepClient(Pep pep) {
        return new PepClient(pep, DOMAIN_VEILARB, Person);
    }

}
