package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.fo.veilarbregistrering.domain.BrukerRegistreringType.SYKMELDT;

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
    BrukerRegistreringType hentType() {
        return SYKMELDT;
    }
}