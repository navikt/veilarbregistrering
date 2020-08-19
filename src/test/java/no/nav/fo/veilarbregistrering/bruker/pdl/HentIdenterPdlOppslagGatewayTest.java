package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.config.CacheConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class HentIdenterPdlOppslagGatewayTest {

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
    public void skalCacheVedKallPaaSammeFnr() throws Exception {
        PdlOppslagGateway pdlOppslagGateway = context.getBean(PdlOppslagGateway.class);
        when(pdlOppslagClient.hentIdenter(any())).thenReturn(dummyPdlIdent());
        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("22222222222"));
        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("22222222222"));
        verify(pdlOppslagClient, times(1)).hentIdenter(any());
    }

    @Test
    public void skalIkkeCacheVedKallPaaForskjelligFnr() throws Exception {
        PdlOppslagGateway pdlOppslagGateway = context.getBean(PdlOppslagGateway.class);
        when(pdlOppslagClient.hentIdenter(any())).thenReturn(dummyPdlIdent());
        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("12345678910"));
        pdlOppslagGateway.hentIdenter(Foedselsnummer.of("109987654321"));
        verify(pdlOppslagClient, times(2)).hentIdenter(any());
    }

    private PdlIdenter dummyPdlIdent() {
        PdlIdent pdlIdent = new PdlIdent();
        pdlIdent.setIdent("12345678910");
        pdlIdent.setHistorisk(false);
        pdlIdent.setGruppe(PdlGruppe.FOLKEREGISTERIDENT);

        PdlIdenter pdlIdenter = new PdlIdenter();
        pdlIdenter.setIdenter(Arrays.asList(pdlIdent));

        return pdlIdenter;
    }

}