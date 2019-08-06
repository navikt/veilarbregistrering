package no.nav.fo.veilarbregistrering.db;

import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.UtdanningSvar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtdanningUtils {
    private static Map<UtdanningSvar, String> nuskodeMap;

    static {
        Map<UtdanningSvar, String> nuskodeMapTmp = new HashMap<>();
        nuskodeMapTmp.put(UtdanningSvar.INGEN_UTDANNING, "0");
        nuskodeMapTmp.put(UtdanningSvar.GRUNNSKOLE, "2");
        nuskodeMapTmp.put(UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING, "3");
        nuskodeMapTmp.put(UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV, "4");
        nuskodeMapTmp.put(UtdanningSvar.HOYERE_UTDANNING_1_TIL_4, "6");
        nuskodeMapTmp.put(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER, "7");
        nuskodeMapTmp.put(UtdanningSvar.INGEN_SVAR, "9");

        nuskodeMap = Collections.unmodifiableMap(nuskodeMapTmp);
    }

    public static String mapTilNuskode(UtdanningSvar utdanning) {
        return nuskodeMap.get(utdanning);
    }

    public static UtdanningSvar mapTilUtdanning(String nuskode) {
        return nuskodeMap.entrySet().stream()
                .filter(e -> e.getValue().equals(nuskode))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
