package no.nav.fo.veilarbregistrering.oppfolging;

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.Besvarelse;

public interface OppfolgingGateway {

    Oppfolgingsstatus hentOppfolgingsstatus(String fodselsnummer);

    void aktiverBruker(String foedselsnummer, Innsatsgruppe innsatsgruppe);

    void reaktiverBruker(String fodselsnummer);

    void settOppfolgingSykmeldt(String fodselsnummer, Besvarelse besvarelse);
}
