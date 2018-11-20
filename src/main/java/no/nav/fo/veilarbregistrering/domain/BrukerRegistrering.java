package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;

import java.time.LocalDateTime;
import java.util.List;

public abstract class BrukerRegistrering {

    abstract BrukerRegistreringType hentType();

}
