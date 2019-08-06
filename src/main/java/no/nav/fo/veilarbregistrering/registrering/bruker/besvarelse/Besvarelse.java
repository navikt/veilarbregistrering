package no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Besvarelse {
    private UtdanningSvar utdanning;
    private UtdanningBestattSvar utdanningBestatt;
    private UtdanningGodkjentSvar utdanningGodkjent;
    private HelseHinderSvar helseHinder;
    private AndreForholdSvar andreForhold;
    private SisteStillingSvar sisteStilling;
    private DinSituasjonSvar dinSituasjon;
    private FremtidigSituasjonSvar fremtidigSituasjon;
    private TilbakeIArbeidSvar tilbakeIArbeid;
}