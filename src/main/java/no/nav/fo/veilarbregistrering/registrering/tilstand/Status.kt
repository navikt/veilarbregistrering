package no.nav.fo.veilarbregistrering.registrering.tilstand

import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat.AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat.AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE
import java.lang.IllegalStateException
import java.util.*

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
        fun parse(status: String): Status {
            return Arrays.stream(values())
                    .filter { s: Status -> s.status == status }
                    .findFirst()
                    .orElseThrow { IllegalStateException() }
        }
        fun from(aktiverBrukerFeil: AktiverBrukerResultat.AktiverBrukerFeil): Status =
            when(aktiverBrukerFeil) {
                BRUKER_MANGLER_ARBEIDSTILLATELSE -> MANGLER_ARBEIDSTILLATELSE
                BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET -> DOD_UTVANDRET_ELLER_FORSVUNNET
                else -> UKJENT_TEKNISK_FEIL
            }
    }
}