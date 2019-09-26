package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StartRegistreringStatus {
    private String maksDato;
    private boolean underOppfolging;
    private boolean erSykmeldtMedArbeidsgiver;
    private Boolean jobbetSeksAvTolvSisteManeder;
    private RegistreringType registreringType;
    private String formidlingsgruppe;
    private String servicegruppe;
}
