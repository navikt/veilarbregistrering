package no.nav.fo.veilarbregistrering.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class OppfolgingStatus {
    private LocalDate inaktiveringsdato;
    private boolean underOppfolging;
}
