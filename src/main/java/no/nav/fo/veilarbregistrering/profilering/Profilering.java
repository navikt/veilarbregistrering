package no.nav.fo.veilarbregistrering.profilering;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.Besvarelse;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Profilering {
    boolean jobbetSammenhengendeSeksAvTolvSisteManeder;
    int alder;
    Innsatsgruppe innsatsgruppe;

    public static Profilering of(Besvarelse besvarelse, int alder, boolean harJobbetSammenhengendeSeksAvTolvSisteManeder) {
        return new Profilering()
                .setAlder(alder)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(harJobbetSammenhengendeSeksAvTolvSisteManeder)
                .setInnsatsgruppe(Innsatsgruppe.of(besvarelse, alder, harJobbetSammenhengendeSeksAvTolvSisteManeder));
    }
}
