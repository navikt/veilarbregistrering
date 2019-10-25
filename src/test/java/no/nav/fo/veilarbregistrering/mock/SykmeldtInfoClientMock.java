package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;

public class SykmeldtInfoClientMock extends SykmeldtInfoClient {

    public SykmeldtInfoClientMock() {
        super(null, null);
    }

    @Override
    public InfotrygdData hentSykmeldtInfoData(String fnr) {
        return new InfotrygdData().withMaksDato("2018-01-01");
    }
}
