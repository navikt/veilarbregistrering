package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering
import org.springframework.http.ResponseEntity

@Tag(name = "RegistreringResource")
interface RegistreringApi {
    @Operation(summary = "Henter oppfølgingsinformasjon om arbeidssøker.")
    fun hentStartRegistreringStatus(): StartRegistreringStatusDto

    @Operation(
        summary = "Starter nyregistrering av arbeidssøker.",
        responses = [
            ApiResponse(responseCode = "200", description = "Registrering OK"),
            ApiResponse(responseCode = "500", description = "Registrering feilet. \n" +
                    "Sjekk *AktiverBrukerFeil* for nærmere detaljer om årsak. Kan ha verdiene: \n" +
                    "- BRUKER_ER_UKJENT: Kastes dersom identen til brukeren ikke finnes registrert i Fellesregistre (AktørID/TPS/NORG). Brukeren kan da heller ikke aktiveres/reaktiveres i Arena.\n" +
                    "- BRUKER_KAN_IKKE_REAKTIVERES: Bruker kan ikke reaktiveres siden bruker har status som aktivert.\n" +
                    "- BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET: Bruker kan ikke aktiveres siden bruker er markert som død/utvandret/forsvunnet.\n" +
                    "- BRUKER_MANGLER_ARBEIDSTILLATELSE: Bruker kan ikke aktiveres automatisk siden bruker ikke har arbeidstillatelse.")
        ])
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