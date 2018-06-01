package no.nav.fo.veilarbregistrering.domain;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class AktiverBrukerData {
    Fnr fnr;
    Boolean selvgande;
}
