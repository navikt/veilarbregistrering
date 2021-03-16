package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

public class BrukerTilstandService {

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
        Oppfolgingsstatus oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        boolean erSykmeldtMedArbeidsgiver = oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(fnr);
        }

        BrukersTilstand brukersTilstand = new BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData);

        if (maksdatoToggletAv()) {
            BrukersTilstandUtenSperret brukersTilstandUtenSperret = new BrukersTilstandUtenSperret(oppfolgingsstatus, sykeforloepMetaData);
            return brukersTilstandUtenSperret;
        }

        return brukersTilstand;
    }

    private boolean maksdatoToggletAv() {
        return unleashService.isEnabled("veilarbregistrering.maksdatoToggletAv");
    }
}
