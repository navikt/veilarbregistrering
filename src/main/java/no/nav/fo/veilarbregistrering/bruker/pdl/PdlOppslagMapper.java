package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static java.util.stream.Collectors.*;

class PdlOppslagMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PdlOppslagMapper.class);

    static Person map(PdlPerson pdlPerson) {
        return Person.of(
                pdlPerson.getSisteOpphold()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                pdlPerson.getSisteStatsborgerskap()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                pdlPerson.hoyestPrioriterteTelefonnummer()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                pdlPerson.getSistePdlFoedsel()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                map(pdlPerson.getGeografiskTilknytning()));
    }

    private static Foedselsdato map(PdlFoedsel pdlFoedsel) {
        return Foedselsdato.of(pdlFoedsel.getFoedselsdato());
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

    private static Telefonnummer map(PdlTelefonnummer pdlTelefonnummer) {
        return Telefonnummer.of(pdlTelefonnummer.getNummer(), pdlTelefonnummer.getLandskode());
    }

    static Identer map(PdlIdenter pdlIdenter) {
        return Identer.of(pdlIdenter.getIdenter().stream()
                .map(pdlIdent -> new Ident(
                        pdlIdent.getIdent(),
                        pdlIdent.isHistorisk(),
                        Gruppe.valueOf(pdlIdent.getGruppe().name())
                ))
                .collect(toList()));
    }

    private static GeografiskTilknytning map(PdlGeografiskTilknytning geografiskTilknytning) {
        if (geografiskTilknytning == null) {
            return null;
        }

        try {
            GtType gtType = geografiskTilknytning.getGtType();
            switch (gtType) {
                case BYDEL:
                    return GeografiskTilknytning.of(geografiskTilknytning.getGtBydel());
                case KOMMUNE:
                    return GeografiskTilknytning.of(geografiskTilknytning.getGtKommune());
                case UTLAND:
                    return GeografiskTilknytning.of(geografiskTilknytning.getGtLand());
            }
            return null;
        } catch (RuntimeException e) {
            LOG.warn(String.format("Mapping av geografisk tilknytning %s fra PDL feilet", geografiskTilknytning), e);
        }
        return null;
    }
}
