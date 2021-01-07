package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AaregRestClientTest {

    private AaregRestClient aaregRestClient = new StubAaregRestClient();

    @Test
    public void skal_parse_formidlingshistorikkResponse() {
        List<ArbeidsforholdDto> arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(Foedselsnummer.of("12345678910"));

        assertThat(arbeidsforholdDtos).hasSize(1);

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

        assertThat(arbeidsforholdDtos).containsOnly(arbeidsforholdDto);
    }
}