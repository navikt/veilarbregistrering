/*
package no.nav.fo.veilarbregistrering.autorisasjon;

import no.nav.common.auth.oidc.OidcTokenValidator;
import no.nav.fo.veilarbregistrering.config.PepConfig;
import org.springframework.stereotype.Component;

@Component
public class AutorisasjonService {



        @Inject
        Provider<HttpServletRequest> httpServletRequestProvider;

        @Inject
        AktorService aktorService;

        @Inject
        PepClient pepClient;

        @Inject
        Pep pep;

        @Inject
        AbacServiceConfig abacServiceConfig;

        private OidcTokenValidator oidcTokenValidator = new OidcTokenValidator();
        private IssoOidcProvider issoProvider = new IssoOidcProvider();



        public void sjekkLesetilgangTilBruker(String fnr) {
            AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);
            pepClient.sjekkLesetilgangTilAktorId(aktorId.getAktorId());
        }

    }
}
*/
