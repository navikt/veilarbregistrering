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
        FlereArbeidsforhold arbeidsforholdFraSoap = hentArbeidsforholdFraSoap(fnr);

        if (arbeidsforholdRestIsEnabled()) {
            FlereArbeidsforhold arbeidsforholdFraRest = hentArbeidsforholdFraRest(fnr);

            if (arbeidsforholdFraSoap.equals(arbeidsforholdFraRest)) {
                LOG.info("Ny og gammel respons er lik");
            } else {
                LOG.info(String.format("SOAP: %s", arbeidsforholdFraSoap.toString()));
                LOG.info(String.format("REST: %s", arbeidsforholdFraRest.toString()));
            }
        }

        return arbeidsforholdFraSoap;
    }

    private FlereArbeidsforhold hentArbeidsforholdFraSoap(Foedselsnummer fnr) {
        LOG.info("Henter arbeidsforhold fra SOAP-tjenesten");
        return soapArbeidsforholdGateway.hentArbeidsforhold(fnr);
    }

    private FlereArbeidsforhold hentArbeidsforholdFraRest(Foedselsnummer fnr) {
        LOG.info("Henter arbeidsforhold fra REST-tjenesten");

        FlereArbeidsforhold flereArbeidsforhold;
        try {
            flereArbeidsforhold = restArbeidsforholdGateway.hentArbeidsforhold(fnr);
        } catch (RuntimeException e) {
            LOG.error("Hent arbeidsforhold via REST feilet: ", e);
            flereArbeidsforhold = FlereArbeidsforhold.of(null);
        }

        return flereArbeidsforhold;
    }

    private boolean arbeidsforholdRestIsEnabled() {
        return unleashService.isEnabled("veilarbregistrering.arbeidsforhold.rest");
    }
}
