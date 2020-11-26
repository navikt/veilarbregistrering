package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.besvarelse.Stilling;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType.ORDINAER;

public class OrdinaerBrukerRegistrering extends BrukerRegistrering {
    long id;
    LocalDateTime opprettetDato;
    Besvarelse besvarelse;
    List<TekstForSporsmal> teksterForBesvarelse;
    Stilling sisteStilling;
    Profilering profilering;

    public OrdinaerBrukerRegistrering() {
    }

    @Override
    public BrukerRegistreringType hentType() {
        return ORDINAER;
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

    public Stilling getSisteStilling() {
        return this.sisteStilling;
    }

    public Profilering getProfilering() {
        return this.profilering;
    }

    public OrdinaerBrukerRegistrering setId(long id) {
        this.id = id;
        return this;
    }

    public OrdinaerBrukerRegistrering setOpprettetDato(LocalDateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public OrdinaerBrukerRegistrering setBesvarelse(Besvarelse besvarelse) {
        this.besvarelse = besvarelse;
        return this;
    }

    public OrdinaerBrukerRegistrering setTeksterForBesvarelse(List<TekstForSporsmal> teksterForBesvarelse) {
        this.teksterForBesvarelse = teksterForBesvarelse;
        return this;
    }

    public OrdinaerBrukerRegistrering setSisteStilling(Stilling sisteStilling) {
        this.sisteStilling = sisteStilling;
        return this;
    }

    public OrdinaerBrukerRegistrering setProfilering(Profilering profilering) {
        this.profilering = profilering;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OrdinaerBrukerRegistrering)) return false;
        final OrdinaerBrukerRegistrering other = (OrdinaerBrukerRegistrering) o;
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
        final Object this$sisteStilling = this.getSisteStilling();
        final Object other$sisteStilling = other.getSisteStilling();
        if (this$sisteStilling == null ? other$sisteStilling != null : !this$sisteStilling.equals(other$sisteStilling))
            return false;
        final Object this$profilering = this.getProfilering();
        final Object other$profilering = other.getProfilering();
        if (this$profilering == null ? other$profilering != null : !this$profilering.equals(other$profilering))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OrdinaerBrukerRegistrering;
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
        final Object $sisteStilling = this.getSisteStilling();
        result = result * PRIME + ($sisteStilling == null ? 43 : $sisteStilling.hashCode());
        final Object $profilering = this.getProfilering();
        result = result * PRIME + ($profilering == null ? 43 : $profilering.hashCode());
        return result;
    }

    public String toString() {
        return "OrdinaerBrukerRegistrering(id=" + this.getId() + ", opprettetDato=" + this.getOpprettetDato() + ", besvarelse=" + this.getBesvarelse() + ", teksterForBesvarelse=" + this.getTeksterForBesvarelse() + ", sisteStilling=" + this.getSisteStilling() + ", profilering=" + this.getProfilering() + ")";
    }
}
