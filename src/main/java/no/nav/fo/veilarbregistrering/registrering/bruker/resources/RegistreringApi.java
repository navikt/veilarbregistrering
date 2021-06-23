package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "RegistreringResource")
public interface RegistreringApi {

    @Operation(summary = "Henter oppfølgingsinformasjon om arbeidssøker.")
    StartRegistreringStatusDto hentStartRegistreringStatus();

    @Operation(summary = "Starter nyregistrering av arbeidssøker.")
    OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering);

    @Operation(summary = "Henter siste registrering av bruker.")
    ResponseEntity<BrukerRegistreringWrapper> hentRegistrering();

    @GetMapping("/registrering")
    ResponseEntity<BrukerRegistreringWrapper> hentPaabegyntRegistrering();

    @Operation(summary = "Starter reaktivering av arbeidssøker.")
    void reaktivering();

    @Operation(summary = "Starter nyregistrering av sykmeldt med arbeidsgiver.")
    void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering);
}
