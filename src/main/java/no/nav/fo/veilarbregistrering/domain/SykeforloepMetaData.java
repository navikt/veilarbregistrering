package no.nav.fo.veilarbregistrering.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

import java.util.Date;

@Wither
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SykeforloepMetaData {
    public boolean erSykmeldt;
    public Date sykmeldtFraDato;
    public boolean erTiltakSykmeldteInngangAktiv;
    public boolean erArbeidsrettetOppfolgingSykmeldtInngangAktiv;
}