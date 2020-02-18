package no.nav.fo.veilarbregistrering.sykemelding;

import no.nav.fo.veilarbregistrering.bruker.AutentiseringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class SykemeldingService {

    private static final Logger LOG = LoggerFactory.getLogger(SykemeldingService.class);

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

            //TODO: Maksdato kan i dag inneholde `null`. Dette bør løses ved at vi i stedet returnerer Optional.
            if (maksdato.asString() == null) {
                LOG.info("Maksdato: null");
            } else {
                LOG.info("{} (maksdato) - {} (dagens dato) = {} (uker sykmeldt)", maksdato, LocalDate.now(), maksdato.antallUkerSykmeldt(LocalDate.now()));
            }

            boolean erSykmeldtOver39Uker = maksdato.beregnSykmeldtMellom39Og52Uker(LocalDate.now());

            sykmeldtInfoData.setMaksDato(maksdato.asString());
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(erSykmeldtOver39Uker);
        }

        return sykmeldtInfoData;
    }

}