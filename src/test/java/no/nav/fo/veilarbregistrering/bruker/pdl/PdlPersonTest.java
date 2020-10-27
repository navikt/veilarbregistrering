package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlAdressebeskyttelse;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlGradering;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlTelefonnummer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PdlPersonTest {

    @Test
    public void hoyestPrioriterteTelefonnummer_skal_returneres() {
        PdlTelefonnummer pdlTelefonnummer1 = new PdlTelefonnummer();
        pdlTelefonnummer1.setPrioritet(1);
        PdlTelefonnummer pdlTelefonnummer2 = new PdlTelefonnummer();
        pdlTelefonnummer2.setPrioritet(2);
        PdlTelefonnummer pdlTelefonnummer3 = new PdlTelefonnummer();
        pdlTelefonnummer3.setPrioritet(3);

        List<PdlTelefonnummer> telefonnummer = Arrays.asList(
                pdlTelefonnummer2, pdlTelefonnummer3, pdlTelefonnummer1);

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setTelefonnummer(telefonnummer);

        Optional<PdlTelefonnummer> pdlTelefonnummer = pdlPerson.hoyestPrioriterteTelefonnummer();
        assertThat(pdlTelefonnummer).hasValue(pdlTelefonnummer1);
    }

    @Test
    public void harAdressebeskyttelse() {
        assertFalse(personMedAdressebeskyttelse().harAdressebeskyttelse());
        assertFalse(personMedAdressebeskyttelse(PdlGradering.UGRADERT).harAdressebeskyttelse());

        assertTrue(personMedAdressebeskyttelse(PdlGradering.FORTROLIG).harAdressebeskyttelse());
        assertTrue(personMedAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG).harAdressebeskyttelse());
        assertTrue(personMedAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG_UTLAND).harAdressebeskyttelse());

        assertTrue(personMedAdressebeskyttelse(PdlGradering.UGRADERT, PdlGradering.FORTROLIG).harAdressebeskyttelse());
    }

    private PdlPerson personMedAdressebeskyttelse(PdlGradering... graderinger) {
        PdlPerson person = new PdlPerson();
        person.setAdressebeskyttelse(Arrays.asList(graderinger).stream()
                .map(PdlAdressebeskyttelse::new)
                .collect(Collectors.toList()));
        return person;
    }
}
