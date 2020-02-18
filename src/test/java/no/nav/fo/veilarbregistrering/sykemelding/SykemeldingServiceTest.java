package no.nav.fo.veilarbregistrering.sykemelding;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SykemeldingServiceTest {

    private SykemeldingService sykemeldingService;

    @Test
    public void hentSykmeldtInfoData_skal_håndtere_maksdato_lik_null() {
        SykemeldingGateway sykemeldingGateway = mock(SykemeldingGateway.class);
        when(sykemeldingGateway.hentReberegnetMaksdato(any())).thenReturn(Maksdato.of(null));

        sykemeldingService = new SykemeldingService(sykemeldingGateway);

        SykmeldtInfoData sykmeldtInfoData = sykemeldingService.hentSykmeldtInfoData(any());
        assertThat(sykmeldtInfoData.getMaksDato()).isNull();
    }

    @Test
    public void hentSykmeldtInfoData_skal_håndtere_maksdato() {
        SykemeldingGateway sykemeldingGateway = mock(SykemeldingGateway.class);
        when(sykemeldingGateway.hentReberegnetMaksdato(any())).thenReturn(Maksdato.of("2020-10-01"));

        sykemeldingService = new SykemeldingService(sykemeldingGateway);

        SykmeldtInfoData sykmeldtInfoData = sykemeldingService.hentSykmeldtInfoData(any());
        assertThat(sykmeldtInfoData.getMaksDato()).isEqualTo("2020-10-01");
    }
}
