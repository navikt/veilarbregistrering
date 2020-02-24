package no.nav.fo.veilarbregistrering.sykemelding;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

public interface SykemeldingGateway {

    Maksdato hentReberegnetMaksdato(Foedselsnummer fnr);
}
