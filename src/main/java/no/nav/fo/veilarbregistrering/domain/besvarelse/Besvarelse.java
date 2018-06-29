package no.nav.fo.veilarbregistrering.domain.besvarelse;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Besvarelse {
    private UtdanningSvar utdanning;
    private UtdanningBestattSvar utdanningBestatt;
    private UtdanningGodkjentSvar utdanningGodkjent;
    private HelseHinderSvar helseHinder;
    private AndreForholdSvar andreForhold;
    private SisteStillingSvar sisteStilling;
    private DinSituasjonSvar dinSituasjon;
}