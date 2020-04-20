package no.nav.fo.veilarbregistrering.kafka;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormdlingsgruppeKafkaConsumerTest {

    @Test
    public void skal_maskere_foedselsnummer_fra_formidlingsgruppeEvent() {
        String maskerEvent = FormidlingsgruppeKafkaConsumer.maskerEvent("PersonId: 32432, Fnr: 23067822521, Formidlingsgruppe: ARBS");
        assertThat(maskerEvent).contains("***********");
        assertThat(maskerEvent).doesNotContain("23067822521");
    }
}
