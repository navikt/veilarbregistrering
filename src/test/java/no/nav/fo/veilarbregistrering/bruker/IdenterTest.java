package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdenterTest {

    @Test
    public void skal_finne_gjeldende_fnr() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("44444444444", true, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        Foedselsnummer fnr = identer.finnGjeldendeFnr();

        assertThat(fnr.stringValue()).isEqualTo("11111111111");
    }

    @Test
    public void skal_finne_gjeldende_aktorid() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("44444444444", true, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        AktorId aktorId = identer.finnGjeldendeAktorId();

        assertThat(aktorId.asString()).isEqualTo("22222222222");
    }

    @Test
    public void tom_liste_skal_gi_notFound() {
        assertThrows(NotFoundException.class, () -> Identer.of(new ArrayList<>()).finnGjeldendeFnr());
        assertThrows(NotFoundException.class, () -> Identer.of(new ArrayList<>()).finnGjeldendeAktorId());
    }

    @Test
    public void ingen_gjeldende_fnr_skal_gi_notFound() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", true, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        assertThrows(NotFoundException.class, identer::finnGjeldendeFnr);
    }

    @Test
    public void ingen_gjeldende_aktorid_skal_gi_notFound() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", true, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        assertThrows(NotFoundException.class, identer::finnGjeldendeAktorId);
    }

    @Test
    public void skal_finne_historiske_fnr() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("44444444444", true, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", true, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID),
                new Ident("55555555555", true, Gruppe.FOLKEREGISTERIDENT)
        ));

        List<Foedselsnummer> historiskeFnr = identer.finnHistoriskeFoedselsnummer();

        assertThat(historiskeFnr.size()).isEqualTo(2);
        assertTrue(historiskeFnr.stream().anyMatch(foedselsnummer -> foedselsnummer.equals(Foedselsnummer.of("44444444444"))));
        assertTrue(historiskeFnr.stream().anyMatch(foedselsnummer -> foedselsnummer.equals(Foedselsnummer.of("55555555555"))));
    }
}
