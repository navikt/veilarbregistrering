package no.nav.fo.veilarbregistrering.enhet.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class GyldighetsperiodeDto {

    private LocalDate fom;
    private LocalDate tom;
}