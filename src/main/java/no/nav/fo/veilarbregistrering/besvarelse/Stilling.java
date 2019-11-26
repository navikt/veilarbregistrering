package no.nav.fo.veilarbregistrering.besvarelse;

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

    public static Stilling tomStilling() {
        return new Stilling("", -1L, "-1");
    }

    public static Stilling ingenYrkesbakgrunn() {
        return new Stilling("X", -1L, "X");
    }
}
