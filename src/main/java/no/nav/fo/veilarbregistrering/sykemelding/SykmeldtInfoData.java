package no.nav.fo.veilarbregistrering.sykemelding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SykmeldtInfoData {
    public String maksDato;
    public boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
}