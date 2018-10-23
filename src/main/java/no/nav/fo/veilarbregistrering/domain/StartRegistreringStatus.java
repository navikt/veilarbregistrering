package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StartRegistreringStatus {
    private boolean underOppfolging;
    private Boolean jobbetSeksAvTolvSisteManeder;
    private Boolean kreverReaktivering;
    private Boolean erIkkeArbeidssokerUtenOppfolging;
    private boolean erSykemeldtMedArbeidsgiverOver39uker;
}
