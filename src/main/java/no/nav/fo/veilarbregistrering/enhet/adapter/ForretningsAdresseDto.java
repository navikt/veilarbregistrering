package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForretningsAdresseDto {

    private String kommunenummer;
    private GyldighetsperiodeDto gyldighetsperiode;
}