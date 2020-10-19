package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertInternalEvent;
import org.junit.Test;

import static no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder.gyldigBesvarelse;
import static no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar.ER_PERMITTERT;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ArbeidssokerRegistrertMapperTest {

    @Test
    public void skal_mappe_opp_alle_verdier() {
        ArbeidssokerRegistrertEvent arbeidssokerRegistrertEvent = ArbeidssokerRegistrertMapper.map(
                new ArbeidssokerRegistrertInternalEvent(
                        AktorId.of("123"),
                        gyldigBrukerRegistrering().setBesvarelse(
                                gyldigBesvarelse().setDinSituasjon(ER_PERMITTERT))));

        assertThat(arbeidssokerRegistrertEvent.getAktorid()).isEqualTo("123");
        assertThat(arbeidssokerRegistrertEvent.getBrukersSituasjon()).isEqualTo("ER_PERMITTERT");
        assertThat(arbeidssokerRegistrertEvent.getRegistreringOpprettet()).isNotNull();
    }
}
