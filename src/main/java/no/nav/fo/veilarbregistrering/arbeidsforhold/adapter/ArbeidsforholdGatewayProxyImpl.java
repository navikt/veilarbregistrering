package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ArbeidsforholdGatewayProxyImpl implements ArbeidsforholdGateway {

    private final static Logger LOG = LoggerFactory.getLogger(ArbeidsforholdGatewayProxyImpl.class);

    private final AaregRestClient aaregRestClient;
    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final UnleashService unleashService;

    public ArbeidsforholdGatewayProxyImpl(
            AaregRestClient aaregRestClient,
            ArbeidsforholdGateway arbeidsforholdGateway, UnleashService unleashService) {
        this.aaregRestClient = aaregRestClient;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.unleashService = unleashService;
    }

    @Override
    public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {
        FlereArbeidsforhold flereArbeidsforhold = hentArbeidsforholdFraSoap(fnr);

        if (arbeidsforholdRestIsEnabled()) {
            FlereArbeidsforhold flereArbeidsforhold1 = hentArbeidsforholdFraRest(fnr);
            if (flereArbeidsforhold.equals(flereArbeidsforhold1)) {
                LOG.info("Ny og gammel respons er lik");
            } else {
                LOG.info(String.format("SOAP: %s", flereArbeidsforhold.toString()));
            }
        }

        return flereArbeidsforhold;
    }

    private FlereArbeidsforhold hentArbeidsforholdFraSoap(Foedselsnummer fnr) {
        LOG.info("Henter arbeidsforhold fra SOAP-tjenesten");
        return arbeidsforholdGateway.hentArbeidsforhold(fnr);
    }

    private FlereArbeidsforhold hentArbeidsforholdFraRest(Foedselsnummer fnr) {
        LOG.info("Henter arbeidsforhold fra REST-tjenesten");
        FlereArbeidsforhold flereArbeidsforhold;
        try {
            List<ArbeidsforholdDto> arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(fnr);
            flereArbeidsforhold = FlereArbeidsforhold.of(arbeidsforholdDtos.stream()
                    .map(ArbeidsforholdMapperV2::map)
                    .collect(Collectors.toList()));
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
