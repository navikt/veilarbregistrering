package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Profilering {
    boolean jobbetSammenhengendeSeksAvTolvSisteManeder;
    int alder;
    Innsatsgruppe innsatsgruppe;
}
