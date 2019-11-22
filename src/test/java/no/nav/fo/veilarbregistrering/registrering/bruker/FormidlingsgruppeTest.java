package no.nav.fo.veilarbregistrering.registrering.bruker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormidlingsgruppeTest {

    @Test
    public void null_skal_gi_nullableFormidlingsgruppe() {
        assertThat(Formidlingsgruppe.of(null)).isInstanceOf(Formidlingsgruppe.NullableFormidlingsgruppe.class);
    }

    @Test
    public void stringValue_for_nullableFormidlingsgruppe_skal_gi_null() {
        Formidlingsgruppe formidlingsgruppe = Formidlingsgruppe.of(null);
        assertThat(formidlingsgruppe.stringValue()).isNull();
    }

    @Test
    public void value_for_nullableFormidlingsgruppe_skal_gi_null() {
        Formidlingsgruppe formidlingsgruppe = Formidlingsgruppe.of(null);
        assertThat(formidlingsgruppe.value()).isEqualTo("INGEN_VERDI");
    }

    @Test
    public void formidlingsgruppe_for_nullableFormidlingsgruppe_skal_gi_null() {
        Formidlingsgruppe formidlingsgruppe = Formidlingsgruppe.of(null);
        assertThat(formidlingsgruppe.fieldName()).isEqualTo("formidlingsgruppe");
    }
}
