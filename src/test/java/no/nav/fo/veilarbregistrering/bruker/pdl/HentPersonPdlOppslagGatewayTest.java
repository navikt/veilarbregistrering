package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;
import no.nav.fo.veilarbregistrering.config.CacheConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@Disabled
class HentPersonPdlOppslagGatewayTest {

    private static PdlOppslagClient pdlOppslagClient;
    private static AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setup() {
        pdlOppslagClient = mock(PdlOppslagClient.class);

        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(PdlOppslagGatewayImpl.class)
                .addConstructorArgValue(pdlOppslagClient)
                .getBeanDefinition();

        context = new AnnotationConfigApplicationContext();
        context.register(CacheConfig.class);
        context.getDefaultListableBeanFactory().registerBeanDefinition("pdlOppslagClient", beanDefinition);
        context.refresh();
        context.start();
    }

    @AfterEach
    public void tearDown() {
        context.stop();
    }

    @Test
    public void skalCacheVedKallPaaSammeAktorId() throws Exception {
        PdlOppslagGateway pdlOppslagGateway = context.getBean(PdlOppslagGateway.class);
        when(pdlOppslagClient.hentPerson(any(AktorId.class))).thenReturn(dummyPdlPerson());
        pdlOppslagGateway.hentPerson(AktorId.of("22222222222"));
        pdlOppslagGateway.hentPerson(AktorId.of("22222222222"));
        verify(pdlOppslagClient, times(1)).hentPerson(any());
    }

    @Test
    public void skalIkkeCacheVedKallPaaForskjelligAktorId() throws Exception {
        PdlOppslagGateway pdlOppslagGateway = context.getBean(PdlOppslagGateway.class);
        when(pdlOppslagClient.hentPerson(any(AktorId.class))).thenReturn(dummyPdlPerson());
        pdlOppslagGateway.hentPerson(AktorId.of("12345678910"));
        pdlOppslagGateway.hentPerson(AktorId.of("109987654321"));
        verify(pdlOppslagClient, times(2)).hentPerson(any());
    }

    private PdlPerson dummyPdlPerson() {
        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));

        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer("94242425", "0047", 0);

        return new PdlPerson(singletonList(pdlTelefonnummer), singletonList(pdlFoedsel), Collections.emptyList());
    }
}
