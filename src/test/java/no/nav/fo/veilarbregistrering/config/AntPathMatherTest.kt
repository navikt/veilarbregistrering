package no.nav.fo.veilarbregistrering.config

import org.junit.Test
import kotlin.test.assertTrue

class OidcAuthenticationFilterMigreringBypassTest {

    @Test
    fun `matcher bare de patterns vi forventer og ikke mer`() {
        val ok = listOf("/api/migrering", "/api/migrering/tjohei")
        val ikkeOk = listOf("/api", "api", "/api/registrering", "/api/sistearbeidsforhold")

        assertTrue {
            ok.all { OidcAuthenticationFilterMigreringBypass.mathcerMigrering(it) }
        }
        assertTrue {
            ikkeOk.none { OidcAuthenticationFilterMigreringBypass.mathcerMigrering(it) }
        }
    }
}