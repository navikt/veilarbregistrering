package no.nav.fo.veilarbregistrering.metrics

enum class Events(override val key: String) : Event {
    AKTIVER_BRUKER("oppfolging.aktiverBruker.event"),
    AKTIVER_BRUKER_FEIL("oppfolging.aktiverBruker.feil.event"),
    REAKTIVER_BRUKER("oppfolging.reaktiverBruker.event"),
    OPPFOLGING_SYKMELDT("oppfolging.sykmeldt.event"),
    OPPFOLGING_FEIL("oppfolging.feil.event"),
    HENT_OPPFOLGING("oppfolging.status.event"),
    OPPGAVE_OPPRETTET_EVENT("arbeid.registrert.oppgave.event"),
    OPPGAVE_ALLEREDE_OPPRETTET_EVENT("arbeid.registrert.oppgave.allerede-opprettet.event"),
    OPPGAVE_ROUTING_EVENT("arbeid.registrert.oppgave.routing.event"),
    OPPGAVE_ROUTING_ENHETSNUMMER_IKKE_FUNNET("oppgave.routing.enhetsnummer.ikke.funnet"),
    START_REGISTRERING_EVENT("arbeid.registrering.start.event"),
    MANUELL_REGISTRERING_EVENT("registrering.manuell-registrering.event"),
    MANUELL_REAKTIVERING_EVENT("registrering.manuell-reaktivering.event"),
    SYKMELDT_BESVARELSE_EVENT("registrering.sykmeldt.besvarelse.event"),
    PROFILERING_EVENT("registrering.bruker.profilering.event"),
    INVALID_REGISTRERING_EVENT("registrering.invalid.registrering.event"),
    HENT_ARBEIDSSOKERPERIODER_KILDE("arbeid.arbeidssoker.kilde.event"),
    HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR("arbeid.arbeidssoker.kilder.gir.samme.svar.event"),
    ORDINAER_BESVARELSE("registrering.besvarelse"),
    BESVARELSE_ALDER("registrering.bruker.alder"),
    BESVARELSE_HELSEHINDER("registrering.besvarelse.helseHinder"),
    BESVARELSE_ANDRE_FORHOLD("registrering.besvarelse.andreForhold"),
    BESVARELSE_HAR_HATT_JOBB_SAMSVARER_M_AAREG("registrering.besvarelse.sistestilling.samsvarermedinfofraaareg");

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