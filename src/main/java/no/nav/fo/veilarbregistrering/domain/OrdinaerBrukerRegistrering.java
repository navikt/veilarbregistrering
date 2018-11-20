package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class OrdinaerBrukerRegistrering extends BrukerRegistrering {
    Stilling sisteStilling;
    Profilering profilering;

    @Override
    public OrdinaerBrukerRegistrering setId(long id){
        this.id = id;
        return this;
    }

    @Override
    public OrdinaerBrukerRegistrering setOpprettetDato(LocalDateTime opprettetDato){
        this.opprettetDato = opprettetDato;
        return this;
    }

    @Override
    public OrdinaerBrukerRegistrering setBesvarelse(Besvarelse besvarelse){
        this.besvarelse = besvarelse;
        return this;
    }

    @Override
    public OrdinaerBrukerRegistrering setTeksterForBesvarelse(List<TekstForSporsmal> teksterForBesvarelse){
        this.teksterForBesvarelse = teksterForBesvarelse;
        return this;
    }

}