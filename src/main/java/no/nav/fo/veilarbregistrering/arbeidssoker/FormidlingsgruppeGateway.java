package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;

import java.util.List;

public interface FormidlingsgruppeGateway {
    Arbeidssokerperioder finnArbeissokerperioder(Foedselsnummer foedselsnummer, Periode periode);
}
