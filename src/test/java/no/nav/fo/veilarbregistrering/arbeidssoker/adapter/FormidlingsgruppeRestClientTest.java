package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class FormidlingsgruppeRestClientTest {

    @Test
    public void skal_parse_formidlingshistorikkResponse() {
        String json = FileToJson.toJson("/arbeidssoker/formidlingshistorikk.json");
        FormidlingsgruppeResponseDto formidlingsgruppeResponseDto = FormidlingsgruppeRestClient.parse(json);

        assertThat(formidlingsgruppeResponseDto.getFodselsnr()).isEqualTo("12345612345");
        assertThat(formidlingsgruppeResponseDto.getPersonId()).isEqualTo("123456");
        assertThat(formidlingsgruppeResponseDto.getFormidlingshistorikk()).hasSize(4);

        FormidlingshistorikkDto formidlingshistorikkDto = new FormidlingshistorikkDto(
                "ISERV",
                "2020-01-12 12:03:24",
                LocalDate.of(2020, 1,12),
                LocalDate.of(2020, 1, 11));
        assertThat(formidlingsgruppeResponseDto.getFormidlingshistorikk()).contains(formidlingshistorikkDto);
    }
}
