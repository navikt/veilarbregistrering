package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.sbl.dialogarena.common.abac.pep.Pep;
import no.nav.sbl.dialogarena.common.abac.pep.context.AbacContext;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({AbacContext.class})
public class PepConfig {

    @Inject
    SystemUserTokenProvider systemUserTokenProvider;

    @Inject
    UnleashService unleashService;

    @Bean
    public VeilarbAbacPepClient pepClient(Pep pep) {

        return VeilarbAbacPepClient.ny()
                .medPep(pep)
                .medResourceTypePerson()
                .medSystemUserTokenProvider(()->systemUserTokenProvider.getSystemUserAccessToken())
                .brukAktoerId(()->unleashService.isEnabled("veilarbregistrering.veilarbabac.aktor"))
                .sammenlikneTilgang(()->unleashService.isEnabled("veilarbregistrering.veilarbabac.sammenlikn"))
                .foretrekkVeilarbAbacResultat(()->unleashService.isEnabled("veilarbregistrering.veilarbabac.foretrekk_veilarbabac"))
                .bygg();
    }

}
