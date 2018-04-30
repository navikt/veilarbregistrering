package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.config.CacheConfig;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArbeidsforholdServiceTest {

    private static ArbeidsforholdV3 arbeidsforholdV3;
    private static AnnotationConfigApplicationContext context;


    @BeforeAll
    public static void setup() {
        arbeidsforholdV3 = mock(ArbeidsforholdV3.class);

        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ArbeidsforholdService.class)
                .addConstructorArgValue(arbeidsforholdV3).getBeanDefinition();

        context = new AnnotationConfigApplicationContext();
        context.register(CacheConfig.class);
        context.getDefaultListableBeanFactory().registerBeanDefinition("arbeidsforhold", beanDefinition);
        context.refresh();
        context.start();
    }

    @AfterAll
    public static void tearDown() {
        context.stop();
    }

    @Test
    public void skalCacheVedKallPaaSammeFnr() throws Exception {
        ArbeidsforholdService arbeidsforholdService = context.getBean(ArbeidsforholdService.class);
        when(arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(any())).thenReturn(new FinnArbeidsforholdPrArbeidstakerResponse());
        arbeidsforholdService.hentArbeidsforhold("fnr");
        arbeidsforholdService.hentArbeidsforhold("fnr");
        verify(arbeidsforholdV3, times(1)).finnArbeidsforholdPrArbeidstaker(any());
    }

    @Test
    public void skalIkkeCacheVedKallPaaForskjelligFnr() throws Exception {
        ArbeidsforholdService arbeidsforholdService = context.getBean(ArbeidsforholdService.class);
        when(arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(any())).thenReturn(new FinnArbeidsforholdPrArbeidstakerResponse());
        arbeidsforholdService.hentArbeidsforhold("fnr");
        arbeidsforholdService.hentArbeidsforhold("fnr2");
        verify(arbeidsforholdV3, times(2)).finnArbeidsforholdPrArbeidstaker(any());
    }

}