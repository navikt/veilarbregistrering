package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

public class SykmeldtInfoClientMock extends SykmeldtInfoClient {

    public SykmeldtInfoClientMock() {
        super(null);
    }

    @Override
    public InfotrygdData hentSykmeldtInfoData(Foedselsnummer fnr) {
        return new InfotrygdData().withMaksDato("2018-01-01");
    }
}
