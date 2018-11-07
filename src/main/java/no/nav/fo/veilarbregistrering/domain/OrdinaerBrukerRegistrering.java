package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class OrdinaerBrukerRegistrering {
    long id;
    LocalDateTime opprettetDato;
    boolean enigIOppsummering;
    String oppsummering;
    Besvarelse besvarelse;
    Stilling sisteStilling;
    List<TekstForSporsmal> teksterForBesvarelse;
}