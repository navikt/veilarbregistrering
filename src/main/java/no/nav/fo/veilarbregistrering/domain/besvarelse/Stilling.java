package no.nav.fo.veilarbregistrering.domain.besvarelse;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Stilling {
    private String label;
    private long konseptId;
    private String styrk08;
}
