package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Veileder {
    String ident;
    NavEnhet enhet;
}
