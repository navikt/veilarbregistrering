package no.nav.fo.veilarbregistrering.bruker.resources;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class KontaktinfoDto {

    private String epost;
    private String telefon;

}
