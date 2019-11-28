package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.oppfolging.Formidlingsgruppe;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormidlingsgruppeTest {

    @Test(expected = IllegalArgumentException.class)
    public void null_skal_gi_illegalArgumentException() {
        assertThat(Formidlingsgruppe.of(null));
    }

    @Test
    public void stringValue_for_nullableFormidlingsgruppe_skal_gi_null() {
        Formidlingsgruppe formidlingsgruppe = Formidlingsgruppe.nullable();
        assertThat(formidlingsgruppe.stringValue()).isNull();
    }

    @Test
    public void value_for_nullableFormidlingsgruppe_skal_gi_null() {
        Formidlingsgruppe formidlingsgruppe = Formidlingsgruppe.nullable();
        assertThat(formidlingsgruppe.value()).isEqualTo("INGEN_VERDI");
    }

    @Test
    public void formidlingsgruppe_for_nullableFormidlingsgruppe_skal_gi_null() {
        Formidlingsgruppe formidlingsgruppe = Formidlingsgruppe.nullable();
        assertThat(formidlingsgruppe.fieldName()).isEqualTo("formidlingsgruppe");
    }
}
