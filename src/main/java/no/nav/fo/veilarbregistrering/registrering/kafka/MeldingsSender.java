package no.nav.fo.veilarbregistrering.registrering.kafka;

public interface MeldingsSender {
    void sendRegistreringsMelding(String aktorId);
}
