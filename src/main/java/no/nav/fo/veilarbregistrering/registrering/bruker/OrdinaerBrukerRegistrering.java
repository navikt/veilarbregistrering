package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.Stilling;
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

}
