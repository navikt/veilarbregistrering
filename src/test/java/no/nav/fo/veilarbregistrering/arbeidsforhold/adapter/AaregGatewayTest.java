package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
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
    public void sak() {
        FlereArbeidsforhold flereArbeidsforhold = aaregGateway.hentArbeidsforhold(Foedselsnummer.of("12345678910"));
        assertThat(flereArbeidsforhold.siste()).isEqualTo(
                new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("981129687")
                        .setStyrk("2130123")
                        .setFom(LocalDate.of(2014, 7, 1))
                        .setTom(LocalDate.of(2015, 12, 31)));
    }

    class StubAaregRestClient extends AaregRestClient {

        StubAaregRestClient() {
            super(null, null);
        }

        @Override
        protected String utforRequest(Foedselsnummer fnr) {
            return FileToJson.toJson("/arbeidsforhold/arbeidsforhold.json");
        }
    }
}
