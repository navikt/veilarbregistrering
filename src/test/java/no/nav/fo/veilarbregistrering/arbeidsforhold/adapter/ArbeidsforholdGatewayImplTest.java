package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ArbeidsforholdGatewayImplTest {

    private ArbeidsforholdGateway arbeidsforholdGateway =
            new ArbeidsforholdGatewayImpl(new StubAaregRestClient());

    @Test
    public void hent_arbeidsforhold_fra_aareg_via_rest() {
        FlereArbeidsforhold flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"));

        assertThat(flereArbeidsforhold).isEqualTo(FlereArbeidsforholdTestdataBuilder.somJson());
    }
}
