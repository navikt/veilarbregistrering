package no.nav.fo.veilarbregistrering.bruker.pdl;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdlError {
    private String message;
    private List<PdlErrorLocation> locations;
    private List<String> path;
    private List<PdlErrorExtension> extensions;
}
