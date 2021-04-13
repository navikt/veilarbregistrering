package no.nav.fo.veilarbregistrering.registrering.formidling

import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil.*

enum class Status(private val status: String) {
    MOTTATT("mottatt"),
    OVERFORT_ARENA("overfort_arena"),
    PUBLISERT_KAFKA("publisertKafka"),
    UKJENT_BRUKER("ukjentBruker"),
    MANGLER_ARBEIDSTILLATELSE("oppholdstillatelse"),
    KAN_IKKE_REAKTIVERES("ikkeReaktivering"),
    DOD_UTVANDRET_ELLER_FORSVUNNET("utvandret"),
    UKJENT_TEKNISK_FEIL("ukjentTeknisk"),
    TEKNISK_FEIL("teknisk"),
    OPPRINNELIG_OPPRETTET_UTEN_TILSTAND("opprinneligOpprettetUtenTilstand");

    companion object {
        @JvmStatic
        fun parse(status: String): Status =
             values().find { s: Status -> s.status == status }
                    ?: throw IllegalStateException("Ukjent Status ble forsøkt parset")

        fun from(aktiverBrukerFeil: AktiverBrukerFeil): Status =
            when(aktiverBrukerFeil) {
                BRUKER_MANGLER_ARBEIDSTILLATELSE -> MANGLER_ARBEIDSTILLATELSE
                BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET -> DOD_UTVANDRET_ELLER_FORSVUNNET
                BRUKER_KAN_IKKE_REAKTIVERES -> KAN_IKKE_REAKTIVERES
                BRUKER_ER_UKJENT -> UKJENT_BRUKER
            }
    }
}
