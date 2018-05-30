package no.nav.fo.veilarbregistrering.domain;

import lombok.*;
import lombok.experimental.Wither;

import java.time.LocalDate;

@Wither
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AktivStatus {
    private boolean aktiv;
    private LocalDate inaktiveringDato;
    private boolean underOppfolging;
}
