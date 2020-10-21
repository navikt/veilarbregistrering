package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar;
import no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertInternalEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

class ArbeidssokerRegistrertMapper {

    static ArbeidssokerRegistrertEvent map(ArbeidssokerRegistrertInternalEvent event) {
        return ArbeidssokerRegistrertEvent.newBuilder()
                .setAktorid(event.getAktorId().asString())
                .setBrukersSituasjon(event.getBrukersSituasjon()
                        .map(Enum::toString)
                        .orElse(null))
                .setRegistreringOpprettet(
                        ZonedDateTime.of(
                                event.getOpprettetDato(),
                                ZoneId.systemDefault()).format(ISO_ZONED_DATE_TIME))
                .setUtdanning(event.getUtdanningSvar()
                        .map(ArbeidssokerRegistrertMapper::map)
                        .orElse(null))
                .setUtdanningBestatt(event.getUtdanningBestattSvar()
                        .map(ArbeidssokerRegistrertMapper::map)
                        .orElse(null))
                .setUtdanningGodkjent(event.getUtdanningGodkjentSvar()
                        .map(ArbeidssokerRegistrertMapper::map)
                        .orElse(null))
                .build();
    }

    private static no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar utdanningGodkjentSvar) {
        switch (utdanningGodkjentSvar) {
            case JA:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.JA;
            case NEI:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.NEI;
            case INGEN_SVAR:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.INGEN_SVAR;
            case VET_IKKE:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.VET_IKKE;
        }
        throw new IllegalArgumentException(String.format("Ukjent svar valgt %s", utdanningGodkjentSvar));
    }

    private static no.nav.arbeid.soker.registrering.UtdanningBestattSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningBestattSvar utdanningBestattSvar) {
        switch (utdanningBestattSvar) {
            case JA:
                return no.nav.arbeid.soker.registrering.UtdanningBestattSvar.JA;
            case NEI:
                return no.nav.arbeid.soker.registrering.UtdanningBestattSvar.NEI;
            case INGEN_SVAR:
                return no.nav.arbeid.soker.registrering.UtdanningBestattSvar.INGEN_SVAR;
        }
        throw new IllegalArgumentException(String.format("Ukjent svar valgt %s", utdanningBestattSvar));
    }

    private static no.nav.arbeid.soker.registrering.UtdanningSvar map(UtdanningSvar utdanningSvar) {
        switch (utdanningSvar) {

            case INGEN_UTDANNING:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.INGEN_UTDANNING;
            case GRUNNSKOLE:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.GRUNNSKOLE;
            case VIDEREGAENDE_GRUNNUTDANNING:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.VIDEREGAENDE_GRUNNUTDANNING;
            case VIDEREGAENDE_FAGBREV_SVENNEBREV:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.VIDEREGAENDE_FAGBREV_SVENNEBREV;
            case HOYERE_UTDANNING_1_TIL_4:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.HOYERE_UTDANNING_1_TIL_4;
            case HOYERE_UTDANNING_5_ELLER_MER:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER;
            case INGEN_SVAR:
                return no.nav.arbeid.soker.registrering.UtdanningSvar.INGEN_SVAR;
        }
        throw new IllegalArgumentException(String.format("Ukjent utdanning valgt %s", utdanningSvar));
    }
}
