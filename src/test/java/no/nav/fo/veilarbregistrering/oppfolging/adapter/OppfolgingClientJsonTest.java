package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OppfolgingClientJsonTest {

    @Test
    public void skal_parse_response_fra_oppfolging() {
        String json = FileToJson.toJson("/oppfolging/aktiverBrukerFeil.json");
        AktiverBrukerFeilDto aktiverBrukerFeilDto = OppfolgingClient.parseResponse(json);
        assertThat(aktiverBrukerFeilDto).isNotNull();
        assertThat(aktiverBrukerFeilDto.getType()).isEqualTo(AktiverBrukerFeilDto.ArenaFeilType.BRUKER_KAN_IKKE_REAKTIVERES);
    }
}
