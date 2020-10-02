package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AaregRestClientTest {

    @Test
    public void skal_parse_formidlingshistorikkResponse() {
        String json = FileToJson.toJson("/arbeidsforhold/arbeidsforhold.json");
        List<ArbeidsforholdDto> arbeidsforholdDtos = AaregRestClient.parse(json);

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
        arbeidsforholdDto.setArbeidsavtaler(Arrays.asList(arbeidsavtaleDto));

        assertThat(arbeidsforholdDtos).containsOnly(arbeidsforholdDto);
    }
}
