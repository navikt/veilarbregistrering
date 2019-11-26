package no.nav.fo.veilarbregistrering.sykemelding;

import no.nav.fo.veilarbregistrering.bruker.AutentiseringUtils;

import java.time.LocalDate;

public class SykemeldingService {

    public final SykemeldingGateway sykemeldingGateway;

    public SykemeldingService(SykemeldingGateway sykemeldingGateway) {
        this.sykemeldingGateway = sykemeldingGateway;
    }

    public SykmeldtInfoData hentSykmeldtInfoData(String fnr) {

        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();

        if (AutentiseringUtils.erVeileder()) {
            // Veiledere har ikke tilgang til å gjøre kall mot infotrygd
            // Sett inngang aktiv, slik at de får registrert sykmeldte brukere
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);

        } else {
            Maksdato maksdato = sykemeldingGateway.hentReberegnetMaksdato(fnr);
            boolean erSykmeldtOver39Uker = maksdato.beregnSykmeldtMellom39Og52Uker(LocalDate.now());

            sykmeldtInfoData.setMaksDato(maksdato.asString());
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(erSykmeldtOver39Uker);
        }

        return sykmeldtInfoData;
    }

}