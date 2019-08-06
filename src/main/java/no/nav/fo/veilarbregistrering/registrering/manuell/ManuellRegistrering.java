package no.nav.fo.veilarbregistrering.registrering.manuell;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class ManuellRegistrering {

    long id;
    long registreringId;
    BrukerRegistreringType brukerRegistreringType;
    String veilederIdent;
    String veilederEnhetId;

}
