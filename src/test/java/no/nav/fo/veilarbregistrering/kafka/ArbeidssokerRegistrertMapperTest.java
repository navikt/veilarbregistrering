package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.arbeid.soker.registrering.UtdanningBestattSvar;
import no.nav.arbeid.soker.registrering.UtdanningGodkjentSvar;
import no.nav.arbeid.soker.registrering.UtdanningSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelse;
import static no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar.ER_PERMITTERT;
import static org.assertj.core.api.Assertions.assertThat;

public class ArbeidssokerRegistrertMapperTest {

    @Test
    public void skal_mappe_opp_alle_verdier() {
        ArbeidssokerRegistrertEvent arbeidssokerRegistrertEvent = ArbeidssokerRegistrertMapper.map(
                new ArbeidssokerRegistrertInternalEvent(
                        AktorId.of("123"),
                        gyldigBesvarelse().setDinSituasjon(ER_PERMITTERT),
                        LocalDateTime.now()));

        assertThat(arbeidssokerRegistrertEvent.getAktorid()).isEqualTo("123");
        assertThat(arbeidssokerRegistrertEvent.getUtdanning()).isEqualTo(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER);
        assertThat(arbeidssokerRegistrertEvent.getUtdanningBestatt()).isEqualTo(UtdanningBestattSvar.JA);
        assertThat(arbeidssokerRegistrertEvent.getUtdanningGodkjent()).isEqualTo(UtdanningGodkjentSvar.JA);
        assertThat(arbeidssokerRegistrertEvent.getBrukersSituasjon()).isEqualTo("ER_PERMITTERT");
        assertThat(arbeidssokerRegistrertEvent.getRegistreringOpprettet()).isNotNull();
    }
}
