package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar

internal object UtdanningUtils {

    private val nuskodeMap: Map<UtdanningSvar, String> = mapOf(
        UtdanningSvar.INGEN_UTDANNING to "0",
        UtdanningSvar.GRUNNSKOLE to "2",
        UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING to "3",
        UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV to "4",
        UtdanningSvar.HOYERE_UTDANNING_1_TIL_4 to "6",
        UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER to "7",
        UtdanningSvar.INGEN_SVAR to "9",
    )

    fun mapTilNuskode(utdanning: UtdanningSvar): String? = nuskodeMap[utdanning]

    fun mapTilUtdanning(nuskode: String): UtdanningSvar? =
        nuskodeMap.filterValues { it == nuskode }.keys.firstOrNull()
}