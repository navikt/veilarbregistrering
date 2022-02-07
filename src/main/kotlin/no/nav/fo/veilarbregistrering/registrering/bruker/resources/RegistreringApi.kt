package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering
import org.springframework.http.ResponseEntity

@Tag(name = "RegistreringResource")
interface RegistreringApi {
    @Operation(summary = "Henter oppfølgingsinformasjon om arbeidssøker.")
    fun hentStartRegistreringStatus(): StartRegistreringStatusDto

    @Operation(summary = "Starter nyregistrering av arbeidssøker.")
    fun registrerBruker(ordinaerBrukerRegistrering: OrdinaerBrukerRegistrering): OrdinaerBrukerRegistrering

    @Operation(summary = "Henter siste registrering av bruker.")
    fun hentRegistrering(): ResponseEntity<BrukerRegistreringWrapper>

    @Operation(summary = "Henter siste påbegynte registrering")
    fun hentPaabegyntRegistrering(): ResponseEntity<BrukerRegistreringWrapper>

    @Operation(summary = "Starter reaktivering av arbeidssøker.")
    fun reaktivering()

    @Operation(summary = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    fun registrerSykmeldt(sykmeldtRegistrering: SykmeldtRegistrering)
}