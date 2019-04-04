package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.FremtidigSituasjonSvar;

@Data
@Accessors(chain = true)
public class FremtidigSituasjonData {
    FremtidigSituasjonSvar alternativId;
    String tekst;
}
