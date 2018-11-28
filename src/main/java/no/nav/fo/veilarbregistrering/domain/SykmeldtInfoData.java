package no.nav.fo.veilarbregistrering.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

import java.time.LocalDateTime;

@Wither
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SykmeldtInfoData {
    public LocalDateTime maksDato;
    public boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
}