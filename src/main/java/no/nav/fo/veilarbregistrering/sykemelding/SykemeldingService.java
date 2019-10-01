package no.nav.fo.veilarbregistrering.sykemelding;

import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.utils.AutentiseringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SykemeldingService {

    public final SykmeldtInfoClient sykmeldtInfoClient;

    public SykemeldingService(SykmeldtInfoClient sykmeldtInfoClient) {
        this.sykmeldtInfoClient = sykmeldtInfoClient;
    }

    public SykmeldtInfoData hentSykmeldtInfoData(String fnr) {

        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();

        if (AutentiseringUtils.erVeileder()) {
            // Veiledere har ikke tilgang til å gjøre kall mot infotrygd
            // Sett inngang aktiv, slik at de får registrert sykmeldte brukere
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
        } else {
            InfotrygdData infotrygdData = sykmeldtInfoClient.hentSykmeldtInfoData(fnr);
            boolean erSykmeldtOver39Uker = beregnSykmeldtMellom39Og52Uker(infotrygdData.maksDato, LocalDate.now());

            sykmeldtInfoData.setMaksDato(infotrygdData.maksDato);
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(erSykmeldtOver39Uker);
        }

        return sykmeldtInfoData;
    }

    static boolean beregnSykmeldtMellom39Og52Uker(String maksDato, LocalDate dagenDato) {
        if (maksDato == null) {
            return false;
        }
        LocalDate dato = LocalDate.parse(maksDato);
        long GJENSTAENDE_UKER = 13;

        return ChronoUnit.WEEKS.between(dagenDato, dato) >= 0 &&
                ChronoUnit.WEEKS.between(dagenDato, dato) <= GJENSTAENDE_UKER;
    }
}