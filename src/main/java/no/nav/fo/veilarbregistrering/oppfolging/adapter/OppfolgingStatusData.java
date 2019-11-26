package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;

@Wither
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OppfolgingStatusData implements Oppfolgingsstatus {
    public boolean underOppfolging;
    public Boolean kanReaktiveres;
    public Boolean erSykmeldtMedArbeidsgiver;
    public String formidlingsgruppe;
    public String servicegruppe;

    @Deprecated
    public Boolean erIkkeArbeidssokerUtenOppfolging;

}