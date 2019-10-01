package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.fo.veilarbregistrering.sykemelding.Maksdato;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;

public class SykemeldingGatewayImpl implements SykemeldingGateway {

    public final SykmeldtInfoClient sykmeldtInfoClient;

    public SykemeldingGatewayImpl(SykmeldtInfoClient sykmeldtInfoClient) {
        this.sykmeldtInfoClient = sykmeldtInfoClient;
    }

    @Override
    public Maksdato hentReberegnetMaksdato(String fnr) {
        InfotrygdData infotrygdData = sykmeldtInfoClient.hentSykmeldtInfoData(fnr);
        return Maksdato.of(infotrygdData.maksDato);
    }
}
