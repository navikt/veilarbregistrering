package no.nav.fo.veilarbregistrering.enhet;

import no.nav.fo.veilarbregistrering.bruker.Periode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganisasjonsdetaljerTest {

    @Test
    public void organisasjonsdetaljer_med_tomme_lister_gir_ingen_kommunenummer() {
        Organisasjonsdetaljer organisasjonsdetaljer = Organisasjonsdetaljer.of(null, null);
        assertThat(organisasjonsdetaljer.kommunenummer()).isEmpty();
    }

    @Test
    public void organisasjonsdetaljer_med_null_skal_handteres_som_tom_liste() {
        Organisasjonsdetaljer organisasjonsdetaljer = Organisasjonsdetaljer.of(null, null);
        assertThat(organisasjonsdetaljer.kommunenummer()).isEmpty();
    }

    @Test
    public void organisasjonsdetaljer_uten_apne_adresser_gir_ingen_kommunenummer() {
        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1234"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 28)));
        List<Forretningsadresse> forretningsadresser = Collections.singletonList(forretningsadresse);
        Postadresse postadresse = new Postadresse(
                Kommunenummer.of("1235"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 28)));
        List<Postadresse> postadresser = Collections.singletonList(postadresse);

        Organisasjonsdetaljer organisasjonsdetaljer = Organisasjonsdetaljer.of(forretningsadresser, postadresser);

        assertThat(organisasjonsdetaljer.kommunenummer()).isEmpty();
    }

    @Test
    public void organisasjonsdetaljer_med_apen_postadresse_skal_gi_kommunenummer_fra_postadresse() {
        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1234"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 28)));
        List<Forretningsadresse> forretningsadresser = Collections.singletonList(forretningsadresse);
        Postadresse postadresse = new Postadresse(
                Kommunenummer.of("1235"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        List<Postadresse> postadresser = Collections.singletonList(postadresse);

        Organisasjonsdetaljer organisasjonsdetaljer = Organisasjonsdetaljer.of(forretningsadresser, postadresser);

        assertThat(organisasjonsdetaljer.kommunenummer()).hasValue(Kommunenummer.of("1235"));
    }

    @Test
    public void organisasjonsdetaljer_med_apen_forretningsadresse_skal_gi_kommunenummer_fra_forretningsadresse() {
        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1234"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        List<Forretningsadresse> forretningsadresser = Collections.singletonList(forretningsadresse);
        Postadresse postadresse = new Postadresse(
                Kommunenummer.of("1235"),
                Periode.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 28)));
        List<Postadresse> postadresser = Collections.singletonList(postadresse);

        Organisasjonsdetaljer organisasjonsdetaljer = Organisasjonsdetaljer.of(forretningsadresser, postadresser);

        assertThat(organisasjonsdetaljer.kommunenummer()).hasValue(Kommunenummer.of("1234"));
    }

    @Test
    public void organisasjonsdetaljer_med_apne_adresser_skal_prioritere_kommunenummer_fra_forretningsadresse() {
        Forretningsadresse forretningsadresse = new Forretningsadresse(
                Kommunenummer.of("1234"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        List<Forretningsadresse> forretningsadresser = Collections.singletonList(forretningsadresse);
        Postadresse postadresse = new Postadresse(
                Kommunenummer.of("1235"),
                Periode.of(LocalDate.of(2020, 1, 1), null));
        List<Postadresse> postadresser = Collections.singletonList(postadresse);

        Organisasjonsdetaljer organisasjonsdetaljer = Organisasjonsdetaljer.of(forretningsadresser, postadresser);

        assertThat(organisasjonsdetaljer.kommunenummer()).hasValue(Kommunenummer.of("1234"));
    }
}
