package no.nav.fo.veilarbregistrering.registrering.reaktivering.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "ReaktiveringResource")
interface ReaktiveringApi {

    @Operation(
        summary = "Reaktiverer bruker som arbeidssøker.",
        description = "Tjenesten gjør en reaktivering av brukere som har blitt inaktivert i løpet av de siste 28 " +
                "dagene. Enkel reaktivering vil si at bruker settes til arbeidssøker (formidlingsgruppe=ARBS) i Arena " +
                "uten at saksbehandler manuelt vurderer reaktiveringen via en arbeidsprosess."
    )
    fun reaktivering()
}