package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;

import java.time.LocalDate;

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
            PdlPerson pdlPerson = new PdlPerson();

            PdlFoedsel pdlFoedsel = new PdlFoedsel();
            pdlFoedsel.setFoedselsdato(foedselsdato);
            pdlPerson.setFoedsel(singletonList(pdlFoedsel));

            PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
            pdlTelefonnummer.setLandskode(landkode_telefonnummer);
            pdlTelefonnummer.setNummer(telefonnummer);
            pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

            return pdlPerson;
        }
    }
}