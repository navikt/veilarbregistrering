package no.nav.fo.veilarbregistrering.domain.besvarelse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class Stilling {
    private String label;
    private long konseptId;
    private String styrk08;
}
