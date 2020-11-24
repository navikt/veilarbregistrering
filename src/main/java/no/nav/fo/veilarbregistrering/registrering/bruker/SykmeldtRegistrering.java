package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

import java.time.LocalDateTime;
import java.util.List;

public class SykmeldtRegistrering extends BrukerRegistrering {
    long id;
    LocalDateTime opprettetDato;
    Besvarelse besvarelse;
    List<TekstForSporsmal> teksterForBesvarelse;

    public SykmeldtRegistrering() {
    }

    @Override
    public BrukerRegistreringType hentType() {
        return BrukerRegistreringType.SYKMELDT;
    }

    public long getId() {
        return this.id;
    }

    public LocalDateTime getOpprettetDato() {
        return this.opprettetDato;
    }

    public Besvarelse getBesvarelse() {
        return this.besvarelse;
    }

    public List<TekstForSporsmal> getTeksterForBesvarelse() {
        return this.teksterForBesvarelse;
    }

    public SykmeldtRegistrering setId(long id) {
        this.id = id;
        return this;
    }

    public SykmeldtRegistrering setOpprettetDato(LocalDateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public SykmeldtRegistrering setBesvarelse(Besvarelse besvarelse) {
        this.besvarelse = besvarelse;
        return this;
    }

    public SykmeldtRegistrering setTeksterForBesvarelse(List<TekstForSporsmal> teksterForBesvarelse) {
        this.teksterForBesvarelse = teksterForBesvarelse;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof SykmeldtRegistrering)) return false;
        final SykmeldtRegistrering other = (SykmeldtRegistrering) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$opprettetDato = this.getOpprettetDato();
        final Object other$opprettetDato = other.getOpprettetDato();
        if (this$opprettetDato == null ? other$opprettetDato != null : !this$opprettetDato.equals(other$opprettetDato))
            return false;
        final Object this$besvarelse = this.getBesvarelse();
        final Object other$besvarelse = other.getBesvarelse();
        if (this$besvarelse == null ? other$besvarelse != null : !this$besvarelse.equals(other$besvarelse))
            return false;
        final Object this$teksterForBesvarelse = this.getTeksterForBesvarelse();
        final Object other$teksterForBesvarelse = other.getTeksterForBesvarelse();
        if (this$teksterForBesvarelse == null ? other$teksterForBesvarelse != null : !this$teksterForBesvarelse.equals(other$teksterForBesvarelse))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SykmeldtRegistrering;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final Object $opprettetDato = this.getOpprettetDato();
        result = result * PRIME + ($opprettetDato == null ? 43 : $opprettetDato.hashCode());
        final Object $besvarelse = this.getBesvarelse();
        result = result * PRIME + ($besvarelse == null ? 43 : $besvarelse.hashCode());
        final Object $teksterForBesvarelse = this.getTeksterForBesvarelse();
        result = result * PRIME + ($teksterForBesvarelse == null ? 43 : $teksterForBesvarelse.hashCode());
        return result;
    }

    public String toString() {
        return "SykmeldtRegistrering(id=" + this.getId() + ", opprettetDato=" + this.getOpprettetDato() + ", besvarelse=" + this.getBesvarelse() + ", teksterForBesvarelse=" + this.getTeksterForBesvarelse() + ")";
    }
}
