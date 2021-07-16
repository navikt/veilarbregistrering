package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.AdressebeskyttelseGradering;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.Gruppe;
import no.nav.fo.veilarbregistrering.bruker.Identer;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlGeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlGtType;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class PdlOppslagMapperTest {

    @Test
    public void skal_mappe_identer() {
        PdlIdent pdlIdent = new PdlIdent();
        pdlIdent.setIdent("12345678910");
        pdlIdent.setHistorisk(false);
        pdlIdent.setGruppe(PdlGruppe.FOLKEREGISTERIDENT);

        PdlIdenter pdlIdenter = new PdlIdenter();
        pdlIdenter.setIdenter(Collections.singletonList(pdlIdent));

        Identer identer = PdlOppslagMapper.map(pdlIdenter);

        assertThat(identer.getIdenter()).hasSize(1);
        assertThat(identer.getIdenter().get(0).getIdent()).isEqualTo("12345678910");
        assertThat(identer.getIdenter().get(0).isHistorisk()).isEqualTo(false);
        assertThat(identer.getIdenter().get(0).getGruppe()).isEqualTo(Gruppe.FOLKEREGISTERIDENT);
    }

    @Test
    public void skal_mappe_adressebeskyttelse() {
        for (PdlGradering gradering : PdlGradering.values()) {
            PdlAdressebeskyttelse pdlAdressebeskyttelse = new PdlAdressebeskyttelse(gradering);
            AdressebeskyttelseGradering mappedGradering = PdlOppslagMapper.map(pdlAdressebeskyttelse);
            assertThat(gradering.name()).isEqualTo(mappedGradering.name());
        }

        assertThat(PdlOppslagMapper.map((PdlAdressebeskyttelse) null)).isEqualTo(AdressebeskyttelseGradering.UKJENT);
    }

    @Test
    public void skal_mappe_person() {
        PdlPerson pdlPerson = new PdlPerson(
                Arrays.asList(
                        new PdlTelefonnummer("11223344", "0043", 3),
                        new PdlTelefonnummer("11223344", "0041", 1),
                        new PdlTelefonnummer("11223344", "0042", 2)),
                Arrays.asList(new PdlFoedsel(LocalDate.of(1950, 12, 31))),
                Arrays.asList());

        Person person = PdlOppslagMapper.map(pdlPerson);
        assertThat(person.harAdressebeskyttelse()).isFalse();
        assertThat(person.getTelefonnummer()).hasValue(Telefonnummer.of("11223344", "0041"));
        assertThat(person.getFoedselsdato()).isEqualTo(Foedselsdato.of(LocalDate.of(1950, 12, 31)));
    }

    @Test
    public void skal_mappe_til_udefinert_til_null(){
        PdlGeografiskTilknytning pdlGeografiskTilknytning = new PdlGeografiskTilknytning(PdlGtType.UDEFINERT, null, null, null);
        GeografiskTilknytning geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning);
        assertThat(geografiskTilknytning).isNull();
    }

    @Test
    public void skal_mappe_kommune_nummer_korrekt(){
        PdlGeografiskTilknytning pdlGeografiskTilknytning = new PdlGeografiskTilknytning(PdlGtType.KOMMUNE, "0144", "", "");
        GeografiskTilknytning geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning);
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("0144"));
    }

    @Test
    public void skal_mappe_bydel_nummer_korrekt(){
        PdlGeografiskTilknytning pdlGeografiskTilknytning = new PdlGeografiskTilknytning(PdlGtType.BYDEL, null, "030102", null);
        GeografiskTilknytning geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning);
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("030102"));

    }

    @Test
    public void skal_mappe_utland_uten_gtLand_til_NOR(){
        PdlGeografiskTilknytning pdlGeografiskTilknytning = new PdlGeografiskTilknytning(PdlGtType.UTLAND, null, null, null);
        GeografiskTilknytning geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning);
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("NOR"));
    }

    @Test
    public void skal_mappe_utland_med_gtLand_til_landkode() {
        PdlGeografiskTilknytning pdlGeografiskTilknytning = new PdlGeografiskTilknytning(PdlGtType.UTLAND, null, null, "POL");
        GeografiskTilknytning geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning);
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("POL"));
    }
}
