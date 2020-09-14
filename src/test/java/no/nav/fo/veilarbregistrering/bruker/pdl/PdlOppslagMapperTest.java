package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.GtType;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.Oppholdstype;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import org.junit.Test;

import java.util.Arrays;

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
    public void skal_mappe_utland_uten_gtLand_til_NOR() {
        PdlPerson pdlPerson = PdlPersonTestdataBuilder
                .basic()
                .geografiskTilknytning(GtType.UTLAND, null)
                .build();

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getGeografiskTilknytning()).hasValue(GeografiskTilknytning.of("NOR"));
    }

    @Test
    public void skal_mappe_utland_med_gtLand_til_landkode() {
        PdlPerson pdlPerson = PdlPersonTestdataBuilder
                .basic()
                .geografiskTilknytning(GtType.UTLAND, "POL")
                .build();

        Person person = PdlOppslagMapper.map(pdlPerson);

        assertThat(person.getGeografiskTilknytning()).hasValue(GeografiskTilknytning.of("POL"));
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

}
