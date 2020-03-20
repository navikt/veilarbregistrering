package no.nav.fo.veilarbregistrering.bruker.pdl;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdlErrorLocation {
    private Integer line;
    private Integer column;
}
