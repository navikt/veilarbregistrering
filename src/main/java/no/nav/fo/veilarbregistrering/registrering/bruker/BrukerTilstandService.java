package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;

public class BrukerTilstandService {

    private final OppfolgingGateway oppfolgingGateway;
    private final SykemeldingService sykemeldingService;

    public BrukerTilstandService(OppfolgingGateway oppfolgingGateway, SykemeldingService sykemeldingService) {
        this.oppfolgingGateway = oppfolgingGateway;
        this.sykemeldingService = sykemeldingService;
    }

    BrukersTilstand hentBrukersTilstand(Foedselsnummer fnr) {
        Oppfolgingsstatus oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        boolean erSykmeldtMedArbeidsgiver = oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(fnr);
        }

        return new BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData);
    }
}
