package no.nav.fo.veilarbregistrering.orgenhet;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.oppgave.TildeltEnhetsnr;

public interface NorgGateway {

    TildeltEnhetsnr hentEnhetFor(Kommunenummer kommunenummer);
}
