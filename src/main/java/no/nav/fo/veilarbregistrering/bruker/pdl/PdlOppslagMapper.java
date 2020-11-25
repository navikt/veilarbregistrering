package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlAdressebeskyttelse;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlFoedsel;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlTelefonnummer;

import static java.util.stream.Collectors.toList;

class PdlOppslagMapper {

    static Person map(PdlPerson pdlPerson) {
        return Person.of(
                pdlPerson.hoyestPrioriterteTelefonnummer()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                pdlPerson.getSistePdlFoedsel()
                        .map(PdlOppslagMapper::map)
                        .orElse(null),
                pdlPerson.strengesteAdressebeskyttelse()
                        .map(PdlOppslagMapper::map)
                        .orElse(AdressebeskyttelseGradering.UKJENT));
    }

    private static Foedselsdato map(PdlFoedsel pdlFoedsel) {
        return Foedselsdato.of(pdlFoedsel.getFoedselsdato());
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

    protected static AdressebeskyttelseGradering map(PdlAdressebeskyttelse adressebeskyttelse) {
        if (adressebeskyttelse == null || adressebeskyttelse.getGradering() == null) {
            return AdressebeskyttelseGradering.UKJENT;
        }
        return AdressebeskyttelseGradering.valueOf(adressebeskyttelse.getGradering().name());
    }
}
