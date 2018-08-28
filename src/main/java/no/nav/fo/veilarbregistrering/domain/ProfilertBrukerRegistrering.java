package no.nav.fo.veilarbregistrering.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProfilertBrukerRegistrering {

    private BrukerRegistrering registrering;

    private Profilering profilering;

}
