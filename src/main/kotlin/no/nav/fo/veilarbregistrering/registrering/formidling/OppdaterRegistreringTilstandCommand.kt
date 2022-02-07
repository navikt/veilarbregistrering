package no.nav.fo.veilarbregistrering.registrering.formidling

class OppdaterRegistreringTilstandCommand private constructor(val id: Long, val status: Status) {

    companion object {
        fun of(id: Long, status: Status): OppdaterRegistreringTilstandCommand {
            return OppdaterRegistreringTilstandCommand(id, status)
        }

        fun of(id: String, status: Status): OppdaterRegistreringTilstandCommand {
            return OppdaterRegistreringTilstandCommand(id.toLong(), status)
        }
    }
}