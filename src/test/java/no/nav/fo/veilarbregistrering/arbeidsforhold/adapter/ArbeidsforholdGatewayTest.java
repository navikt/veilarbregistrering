package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.config.CacheConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

class ArbeidsforholdGatewayTest {

    private static AnnotationConfigApplicationContext context;

    private static AaregRestClient aaregRestClient;

    @BeforeAll
    public static void setup() {
        aaregRestClient = mock(AaregRestClient.class);

        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ArbeidsforholdGatewayImpl.class)
                .addConstructorArgValue(aaregRestClient).getBeanDefinition();

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
    public void skalCacheVedKallPaaSammeFnr() {
        ArbeidsforholdGateway arbeidsforholdGateway = context.getBean(ArbeidsforholdGateway.class);
        when(aaregRestClient.finnArbeidsforhold(any())).thenReturn(Arrays.asList(getArbeidsforholdDto()));
        arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        verify(aaregRestClient, times(1)).finnArbeidsforhold(any());
    }

    @Test
    public void skalIkkeCacheVedKallPaaForskjelligFnr() {
        ArbeidsforholdGateway arbeidsforholdGateway = context.getBean(ArbeidsforholdGateway.class);
        when(aaregRestClient.finnArbeidsforhold(any())).thenReturn(Arrays.asList(getArbeidsforholdDto()));
        arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer.of("109987654321"));
        verify(aaregRestClient, times(2)).finnArbeidsforhold(any());
    }

    private ArbeidsforholdDto getArbeidsforholdDto() {

        ArbeidsforholdDto arbeidsforholdDto = new ArbeidsforholdDto();

        ArbeidsgiverDto arbeidsgiverDto = new ArbeidsgiverDto();
        arbeidsgiverDto.setOrganisasjonsnummer("981129687");
        arbeidsgiverDto.setType("Organisasjon");
        arbeidsforholdDto.setArbeidsgiver(arbeidsgiverDto);

        AnsettelsesperiodeDto ansettelsesperiodeDto = new AnsettelsesperiodeDto();
        PeriodeDto periodeDto = new PeriodeDto();
        periodeDto.setFom("2014-07-01");
        periodeDto.setTom("2015-12-31");
        ansettelsesperiodeDto.setPeriode(periodeDto);
        arbeidsforholdDto.setAnsettelsesperiode(ansettelsesperiodeDto);

        ArbeidsavtaleDto arbeidsavtaleDto = new ArbeidsavtaleDto();
        arbeidsavtaleDto.setYrke("2130123");
        arbeidsforholdDto.setArbeidsavtaler(Collections.singletonList(arbeidsavtaleDto));

        arbeidsforholdDto.setNavArbeidsforholdId(123456);

        return arbeidsforholdDto;
    }
}