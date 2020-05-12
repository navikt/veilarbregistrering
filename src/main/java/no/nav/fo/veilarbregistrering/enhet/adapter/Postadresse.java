package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Postadresse {
    private String kommunenummer;
    private Gyldighetsperiode gyldighetsperiode;
}