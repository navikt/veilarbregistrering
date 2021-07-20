package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;

import java.util.Optional;


class PersonGatewayImpl implements PersonGateway {

    private final PdlOppslagGateway pdlOppslagGateway;

    PersonGatewayImpl(PdlOppslagGateway pdlOppslagGateway) {
        this.pdlOppslagGateway = pdlOppslagGateway;
    }

    @Override
    public Optional<GeografiskTilknytning> hentGeografiskTilknytning(Bruker bruker) {
        Optional<GeografiskTilknytning> geografiskTilknytningPDL = pdlOppslagGateway.hentGeografiskTilknytning(bruker.getAktorId());
        return geografiskTilknytningPDL;
    }

    private static GeografiskTilknytning map(GeografiskTilknytningDto dto) {
        return GeografiskTilknytning.of(dto.getGeografiskTilknytning());
    }
}
