package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.Maksdato;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrukerTilstandService {

    private static final Logger LOG = LoggerFactory.getLogger(BrukerTilstandService.class);

    private final OppfolgingGateway oppfolgingGateway;
    private final SykemeldingService sykemeldingService;
    private final UnleashService unleashService;

    public BrukerTilstandService(
            OppfolgingGateway oppfolgingGateway,
            SykemeldingService sykemeldingService,
            UnleashService unleashService) {
        this.oppfolgingGateway = oppfolgingGateway;
        this.sykemeldingService = sykemeldingService;
        this.unleashService = unleashService;
    }

    BrukersTilstand hentBrukersTilstand(Foedselsnummer fnr) {
        return hentBrukersTilstand(fnr, false);
    }

    BrukersTilstand hentBrukersTilstand(Foedselsnummer fnr, boolean sykmeldtRegistrering) {
        Oppfolgingsstatus oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        boolean erSykmeldtMedArbeidsgiver = oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(fnr);
        }

        BrukersTilstand brukersTilstand = new BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData);
        if (brukAvMaksdato()) {
            LOG.info("Benytter maksdato");
            return brukersTilstand;
        }

        BrukersTilstand brukersTilstandUtenSperret = new BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforloepMetaData);
        loggfoerRegistreringstype(sykmeldtRegistrering, brukersTilstand, brukersTilstandUtenSperret);

        return brukersTilstandUtenSperret;
    }

    private void loggfoerRegistreringstype(boolean sykmeldtRegistrering, BrukersTilstand brukersTilstand, BrukersTilstand brukersTilstandUtenSperret) {
        if (!sykmeldtRegistrering) {
            LOG.info("Benytter ikke maksdato");
            return;
        }

        Maksdato maksdato = maksdato(brukersTilstand);
        LOG.info("Lik registreringstype? {} - når {}",
                brukersTilstand.getRegistreringstype().equals(brukersTilstandUtenSperret.getRegistreringstype()),
                maksdato);
    }

    private Maksdato maksdato(BrukersTilstand brukersTilstand) {
        String maksDato = brukersTilstand.getMaksDato();
        return maksDato != null ? Maksdato.of(maksDato) : Maksdato.nullable();
    }

    private boolean brukAvMaksdato() {
        return !unleashService.isEnabled("veilarbregistrering.maksdatoToggletAv");
    }
}
