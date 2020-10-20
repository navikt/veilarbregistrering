package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.arbeid.soker.registrering.UtdanningBestattSvar;
import no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar;
import no.nav.arbeid.soker.registrering.UtdanningSvar;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertInternalEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.util.Optional.ofNullable;

class ArbeidssokerRegistrertMapper {

    static ArbeidssokerRegistrertEvent map(ArbeidssokerRegistrertInternalEvent event) {
        return ArbeidssokerRegistrertEvent.newBuilder()
                .setAktorid(event.getAktorId().asString())
                .setBrukersSituasjon(ofNullable(event.getBrukersSituasjon())
                        .map(Enum::toString).orElse(null))
                .setRegistreringOpprettet(
                        ZonedDateTime.of(
                                event.getOpprettetDato(),
                                ZoneId.systemDefault()).format(ISO_ZONED_DATE_TIME))
                .setUtdanning(map(event.getUtdanningSvar()))
                .setUtdanningBestatt(map(event.getUtdanningBestattSvar()))
                .setUtdanningGodkjent(map(event.getUtdanningGodkjentSvar()))
                .build();
    }

    private static UtdanningGodkjentSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar utdanningGodkjentSvar) {
        switch (utdanningGodkjentSvar) {
            case JA:
                return UtdanningGodkjentSvar.JA;
            case NEI:
                return UtdanningGodkjentSvar.NEI;
            case INGEN_SVAR:
                return UtdanningGodkjentSvar.INGEN_SVAR;
        }
        throw new IllegalArgumentException(String.format("Ukjent svar valgt %s", utdanningGodkjentSvar));
    }

    private static UtdanningBestattSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar utdanningBestattSvar) {
        switch (utdanningBestattSvar) {
            case JA:
                return UtdanningBestattSvar.JA;
            case NEI:
                return UtdanningBestattSvar.NEI;
            case INGEN_SVAR:
                return UtdanningBestattSvar.INGEN_SVAR;
        }
        throw new IllegalArgumentException(String.format("Ukjent svar valgt %s", utdanningBestattSvar));
    }

    private static UtdanningSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar utdanningSvar) {
        switch (utdanningSvar) {

            case INGEN_UTDANNING:
                return UtdanningSvar.INGEN_UTDANNING;
            case GRUNNSKOLE:
                return UtdanningSvar.GRUNNSKOLE;
            case VIDEREGAENDE_GRUNNUTDANNING:
                return UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING;
            case VIDEREGAENDE_FAGBREV_SVENNEBREV:
                return UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV;
            case HOYERE_UTDANNING_1_TIL_4:
                return UtdanningSvar.HOYERE_UTDANNING_1_TIL_4;
            case HOYERE_UTDANNING_5_ELLER_MER:
                return UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER;
            case INGEN_SVAR:
                return UtdanningSvar.INGEN_SVAR;
        }
        throw new IllegalArgumentException(String.format("Ukjent utdanning valgt %s", utdanningSvar));
    }
}
