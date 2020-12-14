package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArbeidsforholdGatewayProxyImplTest {

    private final UnleashService unleashService = mock(UnleashService.class);

    private ArbeidsforholdGateway arbeidsforholdGatewayProxy = new ArbeidsforholdGatewayProxyImpl(
            new StubAaregRestClient(),
            new ArbeidsforholdGatewayMock(),
            unleashService);

    @Test
    public void hent_arbeidforhold_via_soap() {
        when(unleashService.isEnabled("veilarbregistrering.arbeidsforhold.rest")).thenReturn(false);

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGatewayProxy.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        assertThat(flereArbeidsforhold.siste()).isEqualTo(
                new Arbeidsforhold(
                        null,
                        null,
                        LocalDate.of(2020, 11, 14),
                        null));
    }

    @Disabled
    @Test
    public void hent_arbeidsforhold_fra_aareg_via_rest() {
        when(unleashService.isEnabled("veilarbregistrering.arbeidsforhold.rest")).thenReturn(true);
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGatewayProxy.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        assertThat(flereArbeidsforhold.siste()).isEqualTo(
                new Arbeidsforhold(
                        "981129687",
                        "2130123",
                        LocalDate.of(2014, 7, 1),
                        LocalDate.of(2015, 12, 31)));
    }
}
