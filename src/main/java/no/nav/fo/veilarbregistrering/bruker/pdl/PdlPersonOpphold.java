package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.time.LocalDate;

public class PdlPersonOpphold {
    private Oppholdstype type;
    private LocalDate oppholdFra;
    private LocalDate oppholdTil;


    enum Oppholdstype {
        MIDLERTIDIG ,
        PERMANENT,
        OPPLYSNING_MANGLER
    }

}






