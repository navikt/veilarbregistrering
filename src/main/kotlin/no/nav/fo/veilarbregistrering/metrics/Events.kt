package no.nav.fo.veilarbregistrering.metrics

enum class Events(override val key: String) : Event {
    AKTIVER_BRUKER("oppfolging.aktiverBruker.event"),
    AKTIVER_BRUKER_FEIL("oppfolging.aktiverBruker.feil.event"),
    AUTORISASJON("registrering.autorisasjon.harTilgang.event"),
    REAKTIVER_BRUKER_FEIL("oppfolging.reaktiverBruker.feil.event"),
    REAKTIVER_BRUKER("oppfolging.reaktiverBruker.event"),
    OPPFOLGING_SYKMELDT("oppfolging.sykmeldt.event"),
    OPPFOLGING_SYKMELDT_FEIL("oppfolging.sykmeldt.feil.event"),
    OPPFOLGING_FEIL("oppfolging.feil.event"),
    OPPGAVE_OPPRETTET_EVENT("arbeid.registrert.oppgave.event"),
    OPPGAVE_ALLEREDE_OPPRETTET_EVENT("arbeid.registrert.oppgave.allerede-opprettet.event"),
    OPPGAVE_ROUTING_EVENT("arbeid.registrert.oppgave.routing.event"),
    HENT_BRUKERREGISTRERING_BRUKER_FUNNET("hent.brukerregistrering.bruker.funnet"),
    MANUELL_REGISTRERING_EVENT("registrering.manuell-registrering.event"),
    MANUELL_REAKTIVERING_EVENT("registrering.manuell-reaktivering.event"),
    SYKMELDT_BESVARELSE_EVENT("registrering.sykmeldt.besvarelse.event"),
    PROFILERING_EVENT("registrering.bruker.profilering.event"),
    INVALID_REGISTRERING_EVENT("registrering.invalid.registrering.event"),
    HENT_ARBEIDSSOKERPERIODER_KILDE("arbeid.arbeidssoker.kilde.event"),
    HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR("arbeid.arbeidssoker.kilder.gir.samme.svar.event"),
    KALL_TREDJEPART("timer.tredjepart"),
    REGISTRERING_TOKEN("registrering.token"),
    REGISTRERING_REGISTERINGSTYPE("registrering.registreringstype"),
    REGISTRERING_FULLFORING_REGISTRERINGSTYPE("registrering.fullforing.registreringstype"),
    REGISTRERING_ALLEREDEREGISTRERT("registrering.allerederegistrert"),
    REGISTRERING_NEDETID_ARENA("registrering.nedetid.arena"),
    REGISTRERING_RETTIGHETSGRUPPE("registrering.rettighetsgruppe"),
    REGISTRERING_SERVICEGRUPPE("registrering.servicegruppe"),
    REGISTRERING_TILSTANDSFEIL("registrering.tilstandsfeil")
}

interface Event {
    val key: String

    companion object {
        fun of(key: String): Event = object:Event {
            override val key: String
                get() = key
        }
    }
}