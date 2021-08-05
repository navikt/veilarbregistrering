package no.nav.fo.veilarbregistrering.config

import no.nav.fo.veilarbregistrering.config.OidcAuthenticationFilterMigreringBypass.Companion.MIGRERING_PATTERN
import org.junit.Test
import org.springframework.util.AntPathMatcher
import kotlin.test.assertTrue

class AntPathMatherTest {

    @Test
    fun `AntPathMatcher matcher det vi forventer og ikke mer`() {
        val ok = listOf("/api/migrering")
        val ikkeOk = listOf("/api", "api", "/api/registrering", "/api/sistearbeidsforhold")

        assertTrue {
            ok.all { AntPathMatcher().match(MIGRERING_PATTERN, it) }
        }
        assertTrue {
            ikkeOk.none { AntPathMatcher().match(MIGRERING_PATTERN, it) }
        }
    }
}