package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormidlingsgruppeMapperTest {

    @Test
    public void skal_mappe_json_fra_gg_arena_formidlingsgruppe_v1_til_formidlingsgruppeEvent() {
        /*String record = "{\"table\":\"SIAMO.PERSON\",\"op_type\":\"I\",\"op_ts\":\"2020-04-07 15:46:32.899550\",\"current_ts\":\"2020-04-07T15:51:42.974023\",\"pos\":\"***********001144391\",\"after\":{\"PERSON_ID\":13919,\"FODSELSNR\":\"***********\",\"FORMIDLINGSGRUPPEKODE\":\"ISERV\"}}";
        FormidlingsgruppeEvent formidlingsgruppeEvent = FormidlingsgruppeMapper.map(record);

        assertThat(formidlingsgruppeEvent.getFoedselsnummer().stringValue()).isEqualTo("***********");
        assertThat(formidlingsgruppeEvent.getPerson_id()).isEqualTo("13919");
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppe()).isEqualTo(Formidlingsgruppe.of("ISERV"));*/
    }
}
