package no.nav.fo.veilarbregistrering.registrering.resources;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType;

@Data
@Accessors(chain = true)
public class StartRegistreringStatusDto {
    private String maksDato;
    private boolean underOppfolging;
    private boolean erSykmeldtMedArbeidsgiver;
    private Boolean jobbetSeksAvTolvSisteManeder;
    private RegistreringType registreringType;
    private String formidlingsgruppe;
    private String servicegruppe;
    private String rettighetsgruppe;
    private String geografiskTilknytning;
    private int alder;
}
