package no.nav.fo.veilarbregistrering.bruker.pdl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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






