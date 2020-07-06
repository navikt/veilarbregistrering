package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

import java.time.LocalDate;

public class PdlFoedsel {

    private LocalDate foedselsdato;

    public PdlFoedsel() {
    }

    public LocalDate getFoedselsdato() {
        return foedselsdato;
    }

    public void setFoedselsdato(LocalDate foedselsdato) {
        this.foedselsdato = foedselsdato;
    }
}
