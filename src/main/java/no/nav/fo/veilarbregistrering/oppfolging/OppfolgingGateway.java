package no.nav.fo.veilarbregistrering.oppfolging;

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;

public interface OppfolgingGateway {

    Oppfolgingsstatus hentOppfolgingsstatus(String fodselsnummer);

    void aktiverBruker(String foedselsnummer, Innsatsgruppe innsatsgruppe);

    void reaktiverBruker(String fodselsnummer);

    void settOppfolgingSykmeldt(String fodselsnummer, SykmeldtRegistrering sykmeldtRegistrering);
}
