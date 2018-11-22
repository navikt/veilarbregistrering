package no.nav.fo.veilarbregistrering.domain;

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
    public boolean erSykmeldt;
    public String sykmeldtFraDato;
    public boolean erTiltakSykmeldteInngangAktiv;
    public boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
}