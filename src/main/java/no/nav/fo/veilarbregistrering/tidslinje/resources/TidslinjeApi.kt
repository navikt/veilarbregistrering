package no.nav.fo.veilarbregistrering.tidslinje.resources

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation

@Tag(name = "TidslinjeResource")
interface TidslinjeApi {

    @Operation(summary = "Returnerer en tidslinje over alle historiske hendelser knyttet til arbeidssøkerregistrering")
    fun tidslinje() : TidslinjeDto

}
