package no.nav.fo.veilarbregistrering.domain;


import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProfilertBrukerRegistrering {
    private BrukerRegistrering registrering;
    private Profilering profilering;
    private List<TekstForSporsmal> teksterForBesvarelse; // TODO FO-1451 Skal fjernes.
}
