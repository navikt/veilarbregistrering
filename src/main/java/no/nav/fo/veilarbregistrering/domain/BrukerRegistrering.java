package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;

import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
public class BrukerRegistrering {
    long id;
    String nusKode;
    Date opprettetDato;
    String oppsummering;
    boolean enigIOppsummering;
    Besvarelse besvarelse;
    Stilling sisteStilling;
}