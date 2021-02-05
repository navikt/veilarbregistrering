package no.nav.fo.veilarbregistrering.metrics

enum class Events(override val key: String) : Event {
    OPPGAVE_OPPRETTET_EVENT("arbeid.registrert.oppgave"),
    OPPGAVE_ALLEREDE_OPPRETTET_EVENT("arbeid.registrert.oppgave.allerede-opprettet"),
    OPPGAVE_ROUTING_EVENT("arbeid.registrert.oppgave.routing"),
    START_REGISTRERING_EVENT("arbeid.registrering.start"),
    MANUELL_REGISTRERING_EVENT("registrering.manuell-registrering"),
    MANUELL_REAKTIVERING_EVENT("registrering.manuell-reaktivering"),
    SYKMELDT_BESVARELSE_EVENT("registrering.sykmeldt.besvarelse"),
    PROFILERING_EVENT("registrering.bruker.profilering"),
    INVALID_REGISTRERING_EVENT("registrering.invalid.registrering"),
    MAKSDATO_EVENT("registrering.maksdato"),
    OPPHOLDSTILLATELSE_EVENT("registrering.oppholdstillatelse"),
    HENT_ARBEIDSSOKERPERIODER_KILDE("arbeid.arbeidssoker.kilde"),
    HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR("arbeid.arbeidssoker.kilder.gir.samme.svar"),
    GEOGRAFISK_TILKNYTNING_AVSTEMNING("arbeid.registrering.gt.avstemming"),
    FINN_ARBEIDSFORHOLD_AAREG("finn.arbeidsforhold.aareg");

}

interface Event {
    val key: String

    companion object {
        @JvmStatic
        fun of(key: String): Event = object:Event {
            override val key: String
                get() = key
        }
    }
}