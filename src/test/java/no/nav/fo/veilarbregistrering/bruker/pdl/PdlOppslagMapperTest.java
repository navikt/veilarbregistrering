package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.Opphold;
import no.nav.fo.veilarbregistrering.bruker.Person;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;
import org.junit.Test;

import java.time.LocalDate;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class PdlOppslagMapperTest {

    @Test
    public void skal_mappe_opphold_uten_periode() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(singletonList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));
        pdlPerson.setFoedsel(singletonList(pdlFoedsel));

        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
        pdlTelefonnummer.setLandskode("0047");
        pdlTelefonnummer.setNummer("94242425");
        pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

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
        pdlPerson.setOpphold(singletonList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));
        pdlPerson.setFoedsel(singletonList(pdlFoedsel));
        
        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
        pdlTelefonnummer.setLandskode("0047");
        pdlTelefonnummer.setNummer("94242425");
        pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getOpphold().getType()).isEqualTo(Opphold.Oppholdstype.PERMANENT);
    }
}
