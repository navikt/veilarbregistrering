package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ArenaOppfolging {
    private String formidlingsgruppe;
    private LocalDate inaktiveringsdato;
}
