package no.nav.fo.veilarbregistrering.besvarelse;

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

    public boolean anbefalerBehovForArbeidsevnevurdering() {
        return HelseHinderSvar.JA.equals(helseHinder)
                || AndreForholdSvar.JA.equals(andreForhold);
    }

    public boolean anbefalerStandardInnsats(int alder, boolean oppfyllerKravTilArbeidserfaring) {
        return (18 <= alder && alder <= 59)
                && oppfyllerKravTilArbeidserfaring
                && !UtdanningSvar.INGEN_UTDANNING.equals(utdanning)
                && UtdanningBestattSvar.JA.equals(utdanningBestatt)
                && UtdanningGodkjentSvar.JA.equals(utdanningGodkjent)
                && HelseHinderSvar.NEI.equals(helseHinder)
                && AndreForholdSvar.NEI.equals(andreForhold);
    }
}