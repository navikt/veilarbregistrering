package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArbeidsforholdGatewayImplTest {

    private final UnleashService unleashService = mock(UnleashService.class);

    private ArbeidsforholdGateway arbeidsforholdGatewayProxy =
            new ArbeidsforholdGatewayImpl(new StubAaregRestClient());

    @Test
    public void hent_arbeidsforhold_fra_aareg_via_rest() {
        when(unleashService.isEnabled("veilarbregistrering.arbeidsforhold.rest")).thenReturn(true);

        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGatewayProxy.hentArbeidsforhold(Foedselsnummer.of("12345678910"));

        assertThat(flereArbeidsforhold).isEqualTo(FlereArbeidsforholdTestdataBuilder.somJson());
    }
}
