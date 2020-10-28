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
    public void strengesteAdressebeskyttelse_uten_eksplisitt_graderingsniva() {
        assertThat(personMedAdressebeskyttelse().strengesteAdressebeskyttelse()).isEmpty();
    }

    @Test
    public void strengesteAdressebeskyttelse_med_en_gradering() {
        for (PdlGradering gradering: PdlGradering.values()) {
            PdlPerson enkeltgradertPerson = personMedAdressebeskyttelse(gradering);
            PdlGradering strengesteGradering = strengesteGraderingForPerson(enkeltgradertPerson);

            assertThat(strengesteGradering).isEqualTo(gradering);
        }
    }

    @Test
    public void strengesteAdressebeskyttelse_med_flere_graderinger() {
        assertThat(strengesteGraderingForPerson(
                personMedAdressebeskyttelse(PdlGradering.UGRADERT, PdlGradering.FORTROLIG)
        )).isEqualTo(PdlGradering.FORTROLIG);

        assertThat(strengesteGraderingForPerson(
                personMedAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG, PdlGradering.FORTROLIG)
        )).isEqualTo(PdlGradering.STRENGT_FORTROLIG);

        assertThat(strengesteGraderingForPerson(
                personMedAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG, PdlGradering.STRENGT_FORTROLIG_UTLAND, PdlGradering.FORTROLIG)
        )).isEqualTo(PdlGradering.STRENGT_FORTROLIG_UTLAND);
    }

    private PdlGradering strengesteGraderingForPerson(PdlPerson person) {
        return person.strengesteAdressebeskyttelse()
                .map(PdlAdressebeskyttelse::getGradering).orElse(null);
    }

    private PdlPerson personMedAdressebeskyttelse(PdlGradering... graderinger) {
        PdlPerson person = new PdlPerson();
        person.setAdressebeskyttelse(Arrays.asList(graderinger).stream()
                .map(PdlAdressebeskyttelse::new)
                .collect(Collectors.toList()));
        return person;
    }
}
