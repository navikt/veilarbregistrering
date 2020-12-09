package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.oppfolging.adapter.AktiverBrukerFeilDto

abstract class AktiverBrukerResultat internal constructor() {
    abstract fun erFeil(): Boolean
    abstract fun feil(): AktiverBrukerFeilDto.ArenaFeilType

    companion object {
        fun ok() = object: AktiverBrukerResultat() {
            override fun erFeil(): Boolean = false

            override fun feil(): AktiverBrukerFeilDto.ArenaFeilType {
                throw IllegalStateException("Aktivering gikk Ok - ingen feil finnes")
            }
        }
        fun feilFrom(aktiverBrukerFeilDto: AktiverBrukerFeilDto): AktiverBrukerResultat {
            return object: AktiverBrukerResultat() {
                override fun erFeil(): Boolean = true

                override fun feil(): AktiverBrukerFeilDto.ArenaFeilType = aktiverBrukerFeilDto.type
            }
        }
    }
}