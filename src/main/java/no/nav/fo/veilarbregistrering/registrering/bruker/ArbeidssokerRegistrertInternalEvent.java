package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;

import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;

public class ArbeidssokerRegistrertInternalEvent {

    private final AktorId aktorId;
    private final OrdinaerBrukerRegistrering brukerRegistrering;

    public ArbeidssokerRegistrertInternalEvent(AktorId aktorId, OrdinaerBrukerRegistrering brukerRegistrering) {
        this.aktorId = aktorId;
        this.brukerRegistrering = brukerRegistrering;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public DinSituasjonSvar getBrukersSituasjon() {
        return brukerRegistrering.getBrukersSituasjon();
    }

    public no.nav.arbeid.soker.registrering.UtdanningSvar getUtdanningSvar() {
        return ofNullable(brukerRegistrering.getUtdanningSvar()).map(ArbeidssokerRegistrertInternalEvent::map).orElse(null);
    }

    public no.nav.arbeid.soker.registrering.UtdanningBestattSvar getUtdanningBestattSvar() {
        return ofNullable(brukerRegistrering.getUtdanningBestattSvar()).map(ArbeidssokerRegistrertInternalEvent::map).orElse(null);
    }

    public no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar getUtdanningGodkjentSvar() {
        return ofNullable(brukerRegistrering.getUtdanningGodkjentSvar()).map(ArbeidssokerRegistrertInternalEvent::map).orElse(null);
    }


    public LocalDateTime getOpprettetDato() {
        return brukerRegistrering.getOpprettetDato();
    }


    private static no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningGodkjentSvar utdanningGodkjentSvar) {
        switch (utdanningGodkjentSvar) {
            case JA:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.JA;
            case NEI:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.NEI;
            case INGEN_SVAR:
                return no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar.INGEN_SVAR;
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

    private static no.nav.arbeid.soker.registrering.UtdanningSvar map(no.nav.fo.veilarbregistrering.besvarelse.UtdanningSvar utdanningSvar) {
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
