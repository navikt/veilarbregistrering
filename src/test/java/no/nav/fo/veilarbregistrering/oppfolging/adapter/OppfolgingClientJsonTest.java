package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static no.nav.fo.veilarbregistrering.arbeidssoker.adapter.baseclient.RestUtils.dummyResponseBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class OppfolgingClientJsonTest {

    @Test
    public void skal_parse_response_fra_oppfolging() throws IOException {
        String json = FileToJson.toJson("/oppfolging/kanikkeReaktiveres.json");
        AktiverBrukerFeilDto aktiverBrukerFeilDto = OppfolgingClient.parse(dummyResponseBuilder().code(200).body(ResponseBody.create(MediaType.parse("application/json"), json)).build());
        assertThat(aktiverBrukerFeilDto).isNotNull();
        assertThat(aktiverBrukerFeilDto.getType()).isEqualTo(AktiverBrukerFeilDto.ArenaFeilType.BRUKER_KAN_IKKE_REAKTIVERES);
    }
}
