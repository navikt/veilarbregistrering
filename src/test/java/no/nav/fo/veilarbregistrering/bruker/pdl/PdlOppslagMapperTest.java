package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class PdlOppslagMapperTest {

    @Test
    public void skal_mappe_opphold_uten_periode() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(singletonList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));
        pdlPerson.setFoedsel(singletonList(pdlFoedsel));

        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
        pdlTelefonnummer.setLandskode("0047");
        pdlTelefonnummer.setNummer("94242425");
        pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getStatsborgerskap().getStatsborgerskap()).isEqualTo("NOR");
    }

    @Test
    public void skal_mappe_statsborgerskap_uten_periode() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(singletonList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));
        pdlPerson.setFoedsel(singletonList(pdlFoedsel));
        
        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
        pdlTelefonnummer.setLandskode("0047");
        pdlTelefonnummer.setNummer("94242425");
        pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getOpphold().getType()).isEqualTo(Opphold.Oppholdstype.PERMANENT);
    }

    @Test
    public void skal_mappe_identer() {
        PdlIdent pdlIdent = new PdlIdent();
        pdlIdent.setIdent("12345678910");
        pdlIdent.setHistorisk(false);
        pdlIdent.setGruppe(PdlGruppe.FOLKEREGISTERIDENT);

        PdlIdenter pdlIdenter = new PdlIdenter();
        pdlIdenter.setIdenter(Arrays.asList(pdlIdent));

        Identer identer = PdlOppslagMapper.map(pdlIdenter);

        assertThat(identer.getIdenter()).hasSize(1);
        assertThat(identer.getIdenter().get(0).getIdent()).isEqualTo("12345678910");
        assertThat(identer.getIdenter().get(0).isHistorisk()).isEqualTo(false);
        assertThat(identer.getIdenter().get(0).getGruppe()).isEqualTo(Gruppe.FOLKEREGISTERIDENT);
    }

    @Test
    public void skal_mappe_utland_uten_gtLand_til_NOR() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(singletonList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));
        pdlPerson.setFoedsel(singletonList(pdlFoedsel));

        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
        pdlTelefonnummer.setLandskode("0047");
        pdlTelefonnummer.setNummer("94242425");
        pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

        PdlGeografiskTilknytning geografiskTilknytning = new PdlGeografiskTilknytning();
        geografiskTilknytning.setGtType(GtType.UTLAND);
        pdlPerson.setGeografiskTilknytning(geografiskTilknytning);

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getGeografiskTilknytning()).hasValue(GeografiskTilknytning.of("NOR"));
    }

    @Test
    public void skal_mappe_utland_med_gtLand_til_landkode() {
        PdlPersonOpphold pdlPersonOpphold = new PdlPersonOpphold();
        pdlPersonOpphold.setType(Oppholdstype.PERMANENT);

        PdlStatsborgerskap statsborgerskap = new PdlStatsborgerskap();
        statsborgerskap.setLand("NOR");

        PdlPerson pdlPerson = new PdlPerson();
        pdlPerson.setOpphold(singletonList(pdlPersonOpphold));
        pdlPerson.setStatsborgerskap(singletonList(statsborgerskap));

        PdlFoedsel pdlFoedsel = new PdlFoedsel();
        pdlFoedsel.setFoedselsdato(LocalDate.of(1970, 3, 23));
        pdlPerson.setFoedsel(singletonList(pdlFoedsel));

        PdlTelefonnummer pdlTelefonnummer = new PdlTelefonnummer();
        pdlTelefonnummer.setLandskode("0047");
        pdlTelefonnummer.setNummer("94242425");
        pdlPerson.setTelefonnummer(singletonList(pdlTelefonnummer));

        PdlGeografiskTilknytning geografiskTilknytning = new PdlGeografiskTilknytning();
        geografiskTilknytning.setGtType(GtType.UTLAND);
        geografiskTilknytning.setGtLand("POL");
        pdlPerson.setGeografiskTilknytning(geografiskTilknytning);

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getGeografiskTilknytning()).hasValue(GeografiskTilknytning.of("POL"));
    }
}
