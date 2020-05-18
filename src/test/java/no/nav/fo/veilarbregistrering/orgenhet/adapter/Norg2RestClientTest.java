package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import org.junit.jupiter.api.Test;

import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.KONTAKT_BRUKER;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.OPPFOLGING;
import static org.assertj.core.api.Assertions.assertThat;

public class Norg2RestClientTest {

    @Test
    public void toJson_skal_parse_RsArbeidsfordelingCriteriaDto_to_json() {
        RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto = new RsArbeidsfordelingCriteriaDto();
        rsArbeidsfordelingCriteriaDto.setGeografiskOmraade(Kommunenummer.of("0301").asString());
        rsArbeidsfordelingCriteriaDto.setOppgavetype(KONTAKT_BRUKER);
        rsArbeidsfordelingCriteriaDto.setTema(OPPFOLGING);

        String json = Norg2RestClient.toJson(rsArbeidsfordelingCriteriaDto);

        assertThat(json).isEqualTo("{\"geografiskOmraade\":\"0301\",\"oppgavetype\":\"KONT_BRUK\",\"tema\":\"OPP\"}");
    }
}
