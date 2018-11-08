package no.nav.fo.veilarbregistrering.domain;


import lombok.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProfilertBrukerRegistrering {
    private OrdinaerBrukerRegistrering registrering;
    private Profilering profilering;
}
