package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import no.nav.sbl.featuretoggle.unleash.UnleashService;

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

        return new BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData, maksdatoToggletAv());
    }

    private boolean maksdatoToggletAv() {
        return unleashService.isEnabled("veilarbregistrering.maksdatoToggletAv");
    }
}
