package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.FileToJson;
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FormidlingsgruppeMapperTest {

    @Test
    public void skal_mappe_json_fra_gg_arena_formidlingsgruppe_v1_til_formidlingsgruppeEvent() {
        String json = FileToJson.toJson("/kafka/formidlingsgruppe_uten_mod_dato.json");
        FormidlingsgruppeEvent formidlingsgruppeEvent = InsertAndUpdateFormidlingsgruppeMapper.map(json);

        assertThat(formidlingsgruppeEvent.getFoedselsnummer().get().stringValue()).isEqualTo("***********");
        assertThat(formidlingsgruppeEvent.getPersonId()).isEqualTo("13919");
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppe()).isEqualTo(Formidlingsgruppe.of("ISERV"));
    }

    @Test
    public void skal_mappe_json_med_mod_dato_fra_gg_arena_formidlingsgruppe_v1_til_formidlingsgruppeEvent() {
        String json = FileToJson.toJson("/kafka/formidlingsgruppe_med_mod_dato.json");
        FormidlingsgruppeEvent formidlingsgruppeEvent = InsertAndUpdateFormidlingsgruppeMapper.map(json);

        assertThat(formidlingsgruppeEvent.getFoedselsnummer().get().stringValue()).isEqualTo("***********");
        assertThat(formidlingsgruppeEvent.getPersonId()).isEqualTo("3226568");
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppe()).isEqualTo(Formidlingsgruppe.of("ARBS"));
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppeEndret())
                .isEqualTo(LocalDateTime.of(2020, 6, 19,9,31,50));
    }

    @Test
    public void skal_mappe_json_uten_fnr_fra_gg_arena_formidlingsgruppe_v1_til_formidlingsgruppeEvent() {
        String json = FileToJson.toJson("/kafka/formidlingsgruppe_uten_fnr.json");
        FormidlingsgruppeEvent formidlingsgruppeEvent = InsertAndUpdateFormidlingsgruppeMapper.map(json);

        assertThat(formidlingsgruppeEvent.getFoedselsnummer()).isEmpty();
        assertThat(formidlingsgruppeEvent.getPersonId()).isEqualTo("1652");
        assertThat(formidlingsgruppeEvent.getOperation()).isEqualTo(Operation.INSERT);
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppe()).isEqualTo(Formidlingsgruppe.of("ISERV"));
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppeEndret())
                .isEqualTo(LocalDateTime.of(2007, 12, 3,3,5,54));
    }

    @Test
    public void skal_mappe_bade_after_og_before() {
        String json = FileToJson.toJson("/kafka/formidlingsgruppe_med_mod_dato.json");
        FormidlingsgruppeEvent formidlingsgruppeEvent = InsertAndUpdateFormidlingsgruppeMapper.map(json);

        assertThat(formidlingsgruppeEvent.getFoedselsnummer().get().stringValue()).isEqualTo("***********");
        assertThat(formidlingsgruppeEvent.getPersonId()).isEqualTo("3226568");
        assertThat(formidlingsgruppeEvent.getOperation()).isEqualTo(Operation.UPDATE);
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppe()).isEqualTo(Formidlingsgruppe.of("ARBS"));
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppeEndret())
                .isEqualTo(LocalDateTime.of(2020, 6, 19,9,31,50));
        assertThat(formidlingsgruppeEvent.getForrigeFormidlingsgruppe()).hasValue(Formidlingsgruppe.of("ISERV"));
        assertThat(formidlingsgruppeEvent.getForrigeFormidlingsgruppeEndret())
                .hasValue(LocalDateTime.of(2020, 6, 18,11,13,1));
    }

    @Test
    public void skal_mappe_json_med_opt_type_d_fra_gg_arena_formidlingsgruppe_v1_til_formidlingsgruppeEvent() {
        String json = FileToJson.toJson("/kafka/formidlingsgruppe_med_slett.json");
        FormidlingsgruppeEvent formidlingsgruppeEvent = InsertAndUpdateFormidlingsgruppeMapper.map(json);

        assertThat(formidlingsgruppeEvent.getFoedselsnummer()).isEmpty();
        assertThat(formidlingsgruppeEvent.getPersonId()).isEqualTo("1365747");
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppe()).isEqualTo(Formidlingsgruppe.of("IJOBS"));
        assertThat(formidlingsgruppeEvent.getFormidlingsgruppeEndret())
                .isEqualTo(LocalDateTime.of(2016, 3, 12,0,47,50));
    }
}
