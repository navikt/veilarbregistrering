package no.nav.fo.veilarbregistrering.oppfolging;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;

public interface OppfolgingGateway {

    Oppfolgingsstatus hentOppfolgingsstatus(Foedselsnummer fodselsnummer);

    AktiverBrukerResultat aktiverBruker(Foedselsnummer foedselsnummer, Innsatsgruppe innsatsgruppe);

    AktiverBrukerResultat reaktiverBruker(Foedselsnummer fodselsnummer);

    void settOppfolgingSykmeldt(Foedselsnummer fodselsnummer, Besvarelse besvarelse);
}
