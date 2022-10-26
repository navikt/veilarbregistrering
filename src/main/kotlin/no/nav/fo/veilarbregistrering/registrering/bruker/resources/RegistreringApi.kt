package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "RegistreringResource")
interface RegistreringApi {

    @Operation(summary = "Henter oppfølgingsinformasjon om arbeidssøker.")
    fun hentStartRegistreringStatus(consumerId: String): StartRegistreringStatusDto

    @Operation(summary = "Henter siste registrering av bruker.")
    fun hentRegistrering(): ResponseEntity<BrukerRegistreringWrapper>

}