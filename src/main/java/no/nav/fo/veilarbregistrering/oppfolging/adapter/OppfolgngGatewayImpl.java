package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;

public class OppfolgngGatewayImpl implements OppfolgingGateway {

    private final OppfolgingClient oppfolgingClient;

    public OppfolgngGatewayImpl(OppfolgingClient oppfolgingClient) {
        this.oppfolgingClient = oppfolgingClient;
    }

    @Override
    public Oppfolgingsstatus hentOppfolgingsstatus(String fodselsnummer) {
        return oppfolgingClient.hentOppfolgingsstatus(fodselsnummer);
    }

    @Override
    public void aktiverBruker(String foedselsnummer, Innsatsgruppe innsatsgruppe) {
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(foedselsnummer), innsatsgruppe));
    }

    @Override
    public void reaktiverBruker(String fodselsnummer) {
        oppfolgingClient.reaktiverBruker(fodselsnummer);
    }

    @Override
    public void settOppfolgingSykmeldt(String fodselsnummer, SykmeldtRegistrering sykmeldtRegistrering) {
        oppfolgingClient.settOppfolgingSykmeldt(SykmeldtBrukerType.of(sykmeldtRegistrering), fodselsnummer);
    }

}
