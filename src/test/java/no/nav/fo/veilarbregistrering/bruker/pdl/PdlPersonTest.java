package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlTelefonnummer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
}
