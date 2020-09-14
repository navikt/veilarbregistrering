package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;

import java.time.LocalDate;

import static java.util.Collections.singletonList;

class PdlPersonTestdataBuilder {

    static PdlPersonTestdataBuilder.Builder basic() {
        return new Builder();
    }

    static class Builder {

        private String landkode_telefonnummer = "0047";
        private String telefonnummer = "94242425";
        private Oppholdstype oppholdstype = Oppholdstype.PERMANENT;
        private String landkode = "NOR";
        private LocalDate foedselsdato = LocalDate.of(1970, 3, 23);
        private GtType gtType = GtType.UTLAND;
        private String gtVerdi = null;

        Builder geografiskTilknytning(GtType gtType, String gtVerdi) {
            this.gtType = gtType;
            this.gtVerdi = gtVerdi;
            return this;
        }

        Builder statsborgerskap(String landkode) {
            this.landkode = landkode;
            return this;
        }

        Builder opphold(Oppholdstype oppholdstype) {
            this.oppholdstype = oppholdstype;
            return this;
        }

        PdlPerson build() {
            PdlPerson pdlPerson = new PdlPerson();

            PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
            pdlPersonOpphold.setType(oppholdstype);
            pdlPerson.setOpphold(singletonList(pdlPersonOpphold));

            PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
            statsborgerskap.setLand(landkode);
            pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

            PdlFoedsel pdlFoedsel = new PdlFoedsel();
            pdlFoedsel.setFoedselsdato(foedselsdato);
            pdlPerson.setFoedsel(singletonList(pdlFoedsel));

            PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
            pdlTelefonnummer.setLandskode(landkode_telefonnummer);
            pdlTelefonnummer.setNummer(telefonnummer);
            pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

            PdlGeografiskTilknytning geografiskTilknytning = new PdlGeografiskTilknytning();
            geografiskTilknytning.setGtType(gtType);
            geografiskTilknytning.setGtLand(gtVerdi);
            pdlPerson.setGeografiskTilknytning(geografiskTilknytning);

            return pdlPerson;
        }
    }
}
