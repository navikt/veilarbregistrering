package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.besvarelse.*;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

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

    @Override
    public DinSituasjonSvar getBrukersSituasjon() {
        return besvarelse != null ? besvarelse.getDinSituasjon() : null;
    }

    @Override
    public UtdanningSvar getUtdanningSvar() {
        return besvarelse != null ? besvarelse.getUtdanning() : null;
    }

    @Override
    public UtdanningBestattSvar getUtdanningBestattSvar() {
        return besvarelse != null ? besvarelse.getUtdanningBestatt() : null;
    }

    @Override
    public UtdanningGodkjentSvar getUtdanningGodkjentSvar() {
        return besvarelse != null ? besvarelse.getUtdanningGodkjent() : null;
    }
}
