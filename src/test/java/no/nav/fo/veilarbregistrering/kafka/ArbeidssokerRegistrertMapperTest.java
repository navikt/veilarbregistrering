package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ArbeidssokerRegistrertMapperTest {

    @Test
    public void skal_mappe_opp_alle_verdier() {
        ArbeidssokerRegistrertEvent arbeidssokerRegistrertEvent = ArbeidssokerRegistrertMapper.map(
                AktorId.of("123"),
                DinSituasjonSvar.ER_PERMITTERT,
                LocalDateTime.now());

        assertThat(arbeidssokerRegistrertEvent.getAktorid()).isEqualTo("123");
        assertThat(arbeidssokerRegistrertEvent.getBrukersSituasjon()).isEqualTo("ER_PERMITTERT");
        assertThat(arbeidssokerRegistrertEvent.getRegistreringOpprettet()).isNotNull();
    }
}
