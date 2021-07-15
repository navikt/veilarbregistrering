package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;

import java.time.LocalDate;
import java.util.Collections;

import static java.util.Collections.singletonList;

class PdlPersonTestdataBuilder {

    static PdlPersonTestdataBuilder.Builder basic() {
        return new Builder();
    }

    static class Builder {

        private String landkode_telefonnummer = "0047";
        private String telefonnummer = "94242425";
        private LocalDate foedselsdato = LocalDate.of(1970, 3, 23);

        PdlPerson build() {
            PdlFoedsel pdlFoedsel = new PdlFoedsel();
            pdlFoedsel.setFoedselsdato(foedselsdato);

            PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
            pdlTelefonnummer.setLandskode(landkode_telefonnummer);
            pdlTelefonnummer.setNummer(telefonnummer);

            return new PdlPerson(singletonList(pdlTelefonnummer), singletonList(pdlFoedsel), Collections.emptyList());
        }
    }
}
