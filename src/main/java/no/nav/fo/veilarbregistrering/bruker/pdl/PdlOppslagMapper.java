package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.Opphold;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.bruker.Person;
import no.nav.fo.veilarbregistrering.bruker.Statsborgerskap;

import java.time.LocalDate;

class PdlOppslagMapper {

     static Person map(PdlPerson pdlPerson) {
        return Person.of(
                pdlPerson.getSisteOpphold()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                pdlPerson.getSisteStatsborgerskap()
                        .map(PdlOppslagMapper::map)
                        .orElse(null));
    }

    private static Opphold map(PdlPersonOpphold pdlPersonOpphold) {
        return Opphold.of(
                Opphold.Oppholdstype.valueOf(pdlPersonOpphold.getType().name()),
                mapPeriode(pdlPersonOpphold.getOppholdFra(), pdlPersonOpphold.getOppholdTil()));
    }

    private static Statsborgerskap map(PdlStatsborgerskap pdlStatsborgerskap) {
        return Statsborgerskap.of(
                pdlStatsborgerskap.getLand(),
                mapPeriode(pdlStatsborgerskap.getGyldigFraOgMed(), pdlStatsborgerskap.getGyldigTilOgMed()));
    }

    private static Periode mapPeriode(LocalDate gyldigFraOgMed, LocalDate gyldigTilOgMed) {
        if (gyldigFraOgMed == null && gyldigTilOgMed == null) {
            return null;
        }

        return Periode.of(
                gyldigFraOgMed,
                gyldigTilOgMed);
    }
}
