package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.time.LocalDateTime;

public class RegistreringTilstandTestdataBuilder {

    public static Builder registreringTilstand() {
        return new Builder();
    }

    public static class Builder {

        private long brukerRegistreringId = 112233L;
        private LocalDateTime opprettet = LocalDateTime.now();
        private Status status = Status.MOTTATT;
        private LocalDateTime sistEndret = null;

        public RegistreringTilstand build() {
            return RegistreringTilstand.of(
                    -1L,
                    brukerRegistreringId,
                    opprettet,
                    sistEndret,
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

        public Builder sistEndret(LocalDateTime sistEndret) {
            this.sistEndret = sistEndret;
            return this;
        }
    }
}
