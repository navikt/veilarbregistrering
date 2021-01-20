package no.nav.fo.veilarbregistrering;

import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringJUnitConfig(classes = {ApplicationTestConfig.class})
public class SpringContextTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void spring_context_skal_lastes_uten_feil() {
        assertThat(context).isNotNull();
    }
}
