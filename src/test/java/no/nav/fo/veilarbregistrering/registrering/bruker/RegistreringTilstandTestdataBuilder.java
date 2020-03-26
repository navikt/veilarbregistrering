package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistreringTilstandTestdataBuilder {

    public static Builder registreringTilstand() {
        return new Builder();
    }

    public static class Builder {

        private long brukerRegistreringId = 112233L;
        private LocalDateTime opprettet = LocalDateTime.now();
        private Status status = Status.MOTTATT;

        public RegistreringTilstand build() {
            return RegistreringTilstand.fromDb(
                    -1L,
                    UUID.randomUUID(),
                    brukerRegistreringId,
                    opprettet,
                    null,
                    status);
        }

        public Builder brukerRegistreringId(long brukerRegistreringId) {
            this.brukerRegistreringId = brukerRegistreringId;
            return this;
        }

        public Builder opprettet(LocalDateTime opprettet) {
            this.opprettet = opprettet;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }
    }
}
