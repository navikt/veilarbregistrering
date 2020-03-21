package no.nav.fo.veilarbregistrering.bruker.pdl;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdlResponse {
    private PdlPerson data;
    private List<PdlError> errors;
}
