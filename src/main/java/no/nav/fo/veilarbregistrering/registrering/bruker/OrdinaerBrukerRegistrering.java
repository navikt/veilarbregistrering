package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.besvarelse.*;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType.ORDINAER;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class OrdinaerBrukerRegistrering extends BrukerRegistrering {
    long id;
    LocalDateTime opprettetDato;
    Besvarelse besvarelse;
    List<TekstForSporsmal> teksterForBesvarelse;
    Stilling sisteStilling;
    Profilering profilering;

    @Override
    public BrukerRegistreringType hentType() {
        return ORDINAER;
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
