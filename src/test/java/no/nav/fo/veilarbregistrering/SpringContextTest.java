package no.nav.fo.veilarbregistrering;

import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestConfig.class})
public class SpringContextTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void spring_context_skal_lastes_uten_feil() {

        assertThat(context).isNotNull();
    }
}
