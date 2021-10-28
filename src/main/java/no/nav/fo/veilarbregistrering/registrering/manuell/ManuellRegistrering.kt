package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;

public class ManuellRegistrering {

    long id;
    long registreringId;
    BrukerRegistreringType brukerRegistreringType;
    String veilederIdent;
    String veilederEnhetId;

    public ManuellRegistrering() {
    }

    public long getId() {
        return this.id;
    }

    public long getRegistreringId() {
        return this.registreringId;
    }

    public BrukerRegistreringType getBrukerRegistreringType() {
        return this.brukerRegistreringType;
    }

    public String getVeilederIdent() {
        return this.veilederIdent;
    }

    public String getVeilederEnhetId() {
        return this.veilederEnhetId;
    }

    public ManuellRegistrering setId(long id) {
        this.id = id;
        return this;
    }

    public ManuellRegistrering setRegistreringId(long registreringId) {
        this.registreringId = registreringId;
        return this;
    }

    public ManuellRegistrering setBrukerRegistreringType(BrukerRegistreringType brukerRegistreringType) {
        this.brukerRegistreringType = brukerRegistreringType;
        return this;
    }

    public ManuellRegistrering setVeilederIdent(String veilederIdent) {
        this.veilederIdent = veilederIdent;
        return this;
    }

    public ManuellRegistrering setVeilederEnhetId(String veilederEnhetId) {
        this.veilederEnhetId = veilederEnhetId;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ManuellRegistrering)) return false;
        final ManuellRegistrering other = (ManuellRegistrering) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        if (this.getRegistreringId() != other.getRegistreringId()) return false;
        final Object this$brukerRegistreringType = this.getBrukerRegistreringType();
        final Object other$brukerRegistreringType = other.getBrukerRegistreringType();
        if (this$brukerRegistreringType == null ? other$brukerRegistreringType != null : !this$brukerRegistreringType.equals(other$brukerRegistreringType))
            return false;
        final Object this$veilederIdent = this.getVeilederIdent();
        final Object other$veilederIdent = other.getVeilederIdent();
        if (this$veilederIdent == null ? other$veilederIdent != null : !this$veilederIdent.equals(other$veilederIdent))
            return false;
        final Object this$veilederEnhetId = this.getVeilederEnhetId();
        final Object other$veilederEnhetId = other.getVeilederEnhetId();
        if (this$veilederEnhetId == null ? other$veilederEnhetId != null : !this$veilederEnhetId.equals(other$veilederEnhetId))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ManuellRegistrering;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final long $registreringId = this.getRegistreringId();
        result = result * PRIME + (int) ($registreringId >>> 32 ^ $registreringId);
        final Object $brukerRegistreringType = this.getBrukerRegistreringType();
        result = result * PRIME + ($brukerRegistreringType == null ? 43 : $brukerRegistreringType.hashCode());
        final Object $veilederIdent = this.getVeilederIdent();
        result = result * PRIME + ($veilederIdent == null ? 43 : $veilederIdent.hashCode());
        final Object $veilederEnhetId = this.getVeilederEnhetId();
        result = result * PRIME + ($veilederEnhetId == null ? 43 : $veilederEnhetId.hashCode());
        return result;
    }

    public String toString() {
        return "ManuellRegistrering(id=" + this.getId() + ", registreringId=" + this.getRegistreringId() + ", brukerRegistreringType=" + this.getBrukerRegistreringType() + ", veilederIdent=" + this.getVeilederIdent() + ", veilederEnhetId=" + this.getVeilederEnhetId() + ")";
    }
}
