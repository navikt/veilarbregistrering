package no.nav.fo.veilarbregistrering.domain;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.time.LocalDate;

@Wither
@Value
@Builder
public class OppfolgingStatus {
    private String rettighetsgruppe;
    private String formidlingsgruppe;
    private String servicegruppe;
    private String oppfolgingsenhet;
    private LocalDate inaktiveringsdato;
    private Boolean harMottaOppgaveIArena;
    private boolean oppfolgingsFlaggFO;
}
