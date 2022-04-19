package no.nav.fo.veilarbregistrering.kafka

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEvent
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal object ArbeidssokerRegistrertMapper {
    fun map(event: ArbeidssokerRegistrertInternalEvent): ArbeidssokerRegistrertEvent {
        return ArbeidssokerRegistrertEvent(
            event.aktorId.aktorId,
            event.brukersSituasjon?.name,
            ZonedDateTime.of(
                event.opprettetDato,
                ZoneId.systemDefault()
            ).format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
            event.utdanningSvar?.let(::map),
            event.utdanningBestattSvar?.let(::map),
            event.utdanningGodkjentSvar?.let(::map)
        )
    }

    private fun map(utdanningGodkjentSvar: UtdanningGodkjentSvar): no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar {
        return when (utdanningGodkjentSvar) {
            UtdanningGodkjentSvar.JA -> no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.JA
            UtdanningGodkjentSvar.NEI -> no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.NEI
            UtdanningGodkjentSvar.INGEN_SVAR -> no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.INGEN_SVAR
            UtdanningGodkjentSvar.VET_IKKE -> no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.VET_IKKE
        }
    }

    private fun map(utdanningBestattSvar: UtdanningBestattSvar): no.nav.arbeid.soker.registrering.UtdanningBestattSvar {
        return when (utdanningBestattSvar) {
            UtdanningBestattSvar.JA -> no.nav.arbeid.soker.registrering.UtdanningBestattSvar.JA
            UtdanningBestattSvar.NEI -> no.nav.arbeid.soker.registrering.UtdanningBestattSvar.NEI
            UtdanningBestattSvar.INGEN_SVAR -> no.nav.arbeid.soker.registrering.UtdanningBestattSvar.INGEN_SVAR
        }
    }

    private fun map(utdanningSvar: UtdanningSvar): no.nav.arbeid.soker.registrering.UtdanningSvar {
        return when (utdanningSvar) {
            UtdanningSvar.INGEN_UTDANNING -> no.nav.arbeid.soker.registrering.UtdanningSvar.INGEN_UTDANNING
            UtdanningSvar.GRUNNSKOLE -> no.nav.arbeid.soker.registrering.UtdanningSvar.GRUNNSKOLE
            UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING -> no.nav.arbeid.soker.registrering.UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING
            UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV -> no.nav.arbeid.soker.registrering.UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV
            UtdanningSvar.HOYERE_UTDANNING_1_TIL_4 -> no.nav.arbeid.soker.registrering.UtdanningSvar.HOYERE_UTDANNING_1_TIL_4
            UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER -> no.nav.arbeid.soker.registrering.UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER
            UtdanningSvar.INGEN_SVAR -> no.nav.arbeid.soker.registrering.UtdanningSvar.INGEN_SVAR
        }
    }
}