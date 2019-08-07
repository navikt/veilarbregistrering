package no.nav.fo.veilarbregistrering.profilering;

public interface ProfileringRepository {
    void lagreProfilering(long brukerregistreringId, Profilering profilering);

    Profilering hentProfileringForId(long brukerregistreringId);
}
