package no.nav.fo.veilarbregistrering.orgenhet;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class NavEnhet {
    String id;
    String navn;
}
