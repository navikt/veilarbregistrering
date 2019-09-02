package no.nav.fo.veilarbregistrering.profilering;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Profilering {
    boolean jobbetSammenhengendeSeksAvTolvSisteManeder;
    int alder;
    Innsatsgruppe innsatsgruppe;
}
