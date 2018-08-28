package no.nav.fo.veilarbregistrering.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProfilertBrukerRegistrering {
    private BrukerRegistrering registrering;
    private Profilering profilering;
    private List<TekstForSporsmal> tekstForSporsmal; // TODO FO-1451 Skal fjernes.
}
