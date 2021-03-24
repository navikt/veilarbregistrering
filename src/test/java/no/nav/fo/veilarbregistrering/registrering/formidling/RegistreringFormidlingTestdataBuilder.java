package no.nav.fo.veilarbregistrering.registrering.formidling;

import java.time.LocalDateTime;

public class RegistreringFormidlingTestdataBuilder {

    public static Builder registreringTilstand() {
        return new Builder();
    }

    public static class Builder {

        private long brukerRegistreringId = 112233L;
        private LocalDateTime opprettet = LocalDateTime.now();
        private Status status = Status.MOTTATT;
        private LocalDateTime sistEndret = null;

        public RegistreringFormidling build() {
            return RegistreringFormidling.of(
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
