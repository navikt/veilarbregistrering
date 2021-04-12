package no.nav.fo.veilarbregistrering.tidslinje.resources

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

@Api(value = "TidslinjeResource")
interface TidslinjeApi {

    @ApiOperation(value = "Returnerer en tidslinje over alle historiske hendelser knyttet til arbeidssøkerregistrering")
    fun tidslinje() : TidslinjeDto

}
