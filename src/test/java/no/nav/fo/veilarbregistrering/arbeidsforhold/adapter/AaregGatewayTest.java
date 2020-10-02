package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.AaregGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AaregGatewayTest {

    private AaregGateway aaregGateway = new AaregGatewayImpl(new StubAaregRestClient());

    @Test
    public void hent_arbeidsforhold_fra_aareg_via_rest() {
        FlereArbeidsforhold flereArbeidsforhold = aaregGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        assertThat(flereArbeidsforhold.siste()).isEqualTo(
                new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("981129687")
                        .setStyrk("2130123")
                        .setFom(LocalDate.of(2014, 7, 1))
                        .setTom(LocalDate.of(2015, 12, 31)));
    }
}
