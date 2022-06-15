package no.nav.fo.veilarbregistrering.registrering.sykmeldt.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.fo.veilarbregistrering.registrering.sykmeldt.SykmeldtRegistrering

@Tag(name = "SykmeldtResource")
interface SykmeldtApi {

    @Operation(summary = "Registrerer bruker som `sykmeldt registrert`.")
    fun registrerSykmeldt(sykmeldtRegistrering: SykmeldtRegistrering)
}