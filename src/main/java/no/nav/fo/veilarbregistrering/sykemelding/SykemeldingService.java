package no.nav.fo.veilarbregistrering.sykemelding;

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class SykemeldingService {

    private static final Logger LOG = LoggerFactory.getLogger(SykemeldingService.class);

    private final SykemeldingGateway sykemeldingGateway;
    private final AutorisasjonService autorisasjonService;

    public SykemeldingService(
            SykemeldingGateway sykemeldingGateway,
            AutorisasjonService autorisasjonService) {
        this.sykemeldingGateway = sykemeldingGateway;
        this.autorisasjonService = autorisasjonService;
    }

    public SykmeldtInfoData hentSykmeldtInfoData(Foedselsnummer fnr) {

        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();

        if (autorisasjonService.erInternBruker()) {
            // Veiledere har ikke tilgang til å gjøre kall mot infotrygd
            // Sett inngang aktiv, slik at de får registrert sykmeldte brukere
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);

        } else {
            Maksdato maksdato = sykemeldingGateway.hentReberegnetMaksdato(fnr);

            LOG.info(maksdato.toString());

            boolean erSykmeldtOver39Uker = maksdato.beregnSykmeldtMellom39Og52Uker(LocalDate.now());

            sykmeldtInfoData.setMaksDato(maksdato.asString());
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(erSykmeldtOver39Uker);
        }

        return sykmeldtInfoData;
    }

}