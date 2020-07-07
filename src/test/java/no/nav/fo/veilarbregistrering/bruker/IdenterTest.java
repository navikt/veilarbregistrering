package no.nav.fo.veilarbregistrering.bruker;

import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdenterTest {

    @Test
    public void skal_finne_gjeldende_fnr() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        Foedselsnummer fnr = identer.finnGjeldeneFnr();

        assertThat(fnr.stringValue()).isEqualTo("11111111111");
    }

    @Test
    public void skal_finne_gjeldende_aktorid() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        AktorId aktorId = identer.finnGjeldendeAktorId();

        assertThat(aktorId.asString()).isEqualTo("22222222222");
    }

    @Test
    public void tom_liste_skal_gi_notFound() {
        assertThrows(NotFoundException.class, () -> Identer.of(new ArrayList<>()).finnGjeldeneFnr());
        assertThrows(NotFoundException.class, () -> Identer.of(new ArrayList<>()).finnGjeldendeAktorId());
    }

    @Test
    public void ingen_gjeldende_fnr_skal_gi_notFound() {
        Identer identer = Identer.of(Arrays.asList(
                new Ident("11111111111", true, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        ));

        assertThrows(NotFoundException.class, identer::finnGjeldeneFnr);
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
}
