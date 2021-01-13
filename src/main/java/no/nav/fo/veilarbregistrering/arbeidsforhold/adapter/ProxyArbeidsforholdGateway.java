package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyArbeidsforholdGateway implements ArbeidsforholdGateway {

    private final static Logger LOG = LoggerFactory.getLogger(ProxyArbeidsforholdGateway.class);

    private final ArbeidsforholdGateway soapArbeidsforholdGateway;
    private final ArbeidsforholdGateway restArbeidsforholdGateway;
    private final UnleashService unleashService;

    public ProxyArbeidsforholdGateway(
            ArbeidsforholdGateway soapArbeidsforholdGateway,
            ArbeidsforholdGateway restArbeidsforholdGateway,
            UnleashService unleashService) {
        this.soapArbeidsforholdGateway = soapArbeidsforholdGateway;
        this.restArbeidsforholdGateway = restArbeidsforholdGateway;
        this.unleashService = unleashService;
    }

    @Override
    public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {
        FlereArbeidsforhold flereArbeidsforhold;
        if (arbeidsforholdRestIsEnabled()) {
            flereArbeidsforhold = hentArbeidsforholdFraRest(fnr);
        } else {
            flereArbeidsforhold = hentArbeidsforholdFraSoap(fnr);
        }

        return flereArbeidsforhold;
    }

    private FlereArbeidsforhold hentArbeidsforholdFraSoap(Foedselsnummer fnr) {
        LOG.info("Henter arbeidsforhold fra SOAP-tjenesten");
        return soapArbeidsforholdGateway.hentArbeidsforhold(fnr);
    }

    private FlereArbeidsforhold hentArbeidsforholdFraRest(Foedselsnummer fnr) {
        LOG.info("Henter arbeidsforhold fra REST-tjenesten");
        return restArbeidsforholdGateway.hentArbeidsforhold(fnr);
    }

    private boolean arbeidsforholdRestIsEnabled() {
        return unleashService.isEnabled("veilarbregistrering.arbeidsforhold.rest");
    }
}
