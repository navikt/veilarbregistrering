package no.nav.fo.veilarbregistrering.profilering.resources

import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "ProfileringResource")
interface ProfileringApi {
    fun hentProfileringForBurker(): ProfileringDto
}
