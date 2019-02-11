package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class ManuellRegistrering {

    long id;
    String aktorId;
    String veilederIdent;
    String veilederEnhetId;

}
