package no.nav.fo.veilarbregistrering.arbeidsforhold;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ARBEIDSFORHOLD;

public interface ArbeidsforholdGateway {
    @Cacheable(HENT_ARBEIDSFORHOLD)
    List<Arbeidsforhold> hentArbeidsforhold(String fnr);

    Arbeidsforhold hentSisteArbeidsforhold(String fnr);
}
