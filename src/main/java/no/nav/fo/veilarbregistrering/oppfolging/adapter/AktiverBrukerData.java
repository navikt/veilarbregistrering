package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AktiverBrukerData {
    Fnr fnr;
    Innsatsgruppe innsatsgruppe;
}
