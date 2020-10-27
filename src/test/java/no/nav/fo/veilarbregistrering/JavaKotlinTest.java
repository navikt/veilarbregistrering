package no.nav.fo.veilarbregistrering;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaKotlinTest {

    @Test
    public void javaTest() {
        PdlPerson person = KotlinThing.INSTANCE.kotlinPerson();
        assertThat(person).isNotNull();
    }
}
