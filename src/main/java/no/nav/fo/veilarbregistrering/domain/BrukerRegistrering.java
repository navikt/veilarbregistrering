package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;

import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class BrukerRegistrering {
    long id;
    String nusKode;
    Date opprettetDato;
    boolean enigIOppsummering;
    String oppsummering;
    Besvarelse besvarelse;
    Stilling sisteStilling;
}