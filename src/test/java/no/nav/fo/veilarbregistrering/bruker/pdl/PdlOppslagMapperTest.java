package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PdlOppslagMapperTest {

    @Test
    public void skal_mappe_opphold_uten_periode() {
        PdlPerson pdlPerson = PdlPersonTestdataBuilder
                .basic()
                .opphold(Oppholdstype.PERMANENT)
                .build();

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getStatsborgerskap().getStatsborgerskap()).isEqualTo("NOR");
    }

    @Test
    public void skal_mappe_statsborgerskap_uten_periode() {
        PdlPerson pdlPerson = PdlPersonTestdataBuilder
                .basic()
                .statsborgerskap("NOR")
                .build();

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

}
