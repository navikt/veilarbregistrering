package no.nav.fo.veilarbregistrering.bruker.aktor;

import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.bruker.AktorGateway;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

class AktorGatewayImpl implements AktorGateway {

    private final AktorService aktorService;

    AktorGatewayImpl(AktorService aktorService) {
        this.aktorService = aktorService;
    }

    @Override
    public AktorId hentAktorIdFor(Foedselsnummer fnr) {
        return AktorId.of(aktorService.getAktorId(fnr.stringValue())
                .orElseThrow(() -> new Feil(FeilType.FINNES_IKKE)));
    }

}
