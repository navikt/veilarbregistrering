package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class SykmeldtRegistrering extends BrukerRegistrering {
    long id;
    LocalDateTime opprettetDato;
    Besvarelse besvarelse;
    List<TekstForSporsmal> teksterForBesvarelse;

    @Override
    public BrukerRegistreringType hentType() {
        return BrukerRegistreringType.SYKMELDT;
    }

}
