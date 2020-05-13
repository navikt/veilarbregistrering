package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;

import java.util.Optional;

public interface HentEnhetsIdForSisteArbeidsforhold {

    Optional<Enhetsnr> hentEnhetsnummerForSisteArbeidsforholdTil(Bruker bruker);
}
