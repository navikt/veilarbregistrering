package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.Person;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PdlOppslagMapperTest {

    @Test
    public void skal_mappe_opphold_uten_periode() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(Arrays.asList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(Arrays.asList(statsborgerskap));

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getStatsborgerskap().getStatsborgerskap()).isEqualTo("NOR");
    }

    @Test
    public void skal_mappe_statsborgerskap_uten_periode() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(Arrays.asList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(Arrays.asList(statsborgerskap));

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getOpphold().getType()).isEqualTo(Person.Oppholdstype.PERMANENT);
    }
}
