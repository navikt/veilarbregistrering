package no.nav.fo.veilarbregistrering.featuretoggle.resources

import no.nav.common.featuretoggle.UnleashClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FeaturetoggleResource(private val unleashClient: UnleashClient) {

    @GetMapping("/api/featuretoggle")
    fun hentFeatureToggles(@RequestParam("feature") vararg featureName: String): Map<String, Boolean> {
        return featureName.associateWith { unleashClient.isEnabled(it) }
    }
}