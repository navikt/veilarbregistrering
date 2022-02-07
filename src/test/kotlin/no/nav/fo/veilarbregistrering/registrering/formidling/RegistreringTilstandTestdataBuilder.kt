package no.nav.fo.veilarbregistrering.registrering.formidling

import java.time.LocalDateTime

object RegistreringTilstandTestdataBuilder {

    @JvmStatic
    fun registreringTilstand() = Builder()

    class Builder {
        private var brukerRegistreringId = 112233L
        private var opprettet = LocalDateTime.now()
        private var status = Status.MOTTATT
        private var sistEndret: LocalDateTime? = null
        fun build(): RegistreringTilstand {
            return RegistreringTilstand.of(
                -1L,
                brukerRegistreringId,
                opprettet,
                sistEndret,
                status
            )
        }

        fun brukerRegistreringId(brukerRegistreringId: Long): Builder {
            this.brukerRegistreringId = brukerRegistreringId
            return this
        }

        fun opprettet(opprettet: LocalDateTime): Builder {
            this.opprettet = opprettet
            return this
        }

        fun status(status: Status): Builder {
            this.status = status
            return this
        }

        fun sistEndret(sistEndret: LocalDateTime?): Builder {
            this.sistEndret = sistEndret
            return this
        }
    }
}
