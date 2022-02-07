package no.nav.fo.veilarbregistrering.tidslinje.resources

import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeAggregator
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeElement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/tidslinje")
class TidslinjeResource(
        private val autorisasjonService: AutorisasjonService,
        private val userService: UserService,
        private val tidslinjeAggregator: TidslinjeAggregator) : TidslinjeApi {

    @GetMapping
    override fun tidslinje() : TidslinjeDto {
        val bruker: Bruker = userService.finnBrukerGjennomPdl()
        autorisasjonService.sjekkLesetilgangTilBruker(bruker.aktorId)

        return TidslinjeDto(tidslinjeAggregator.tidslinje(bruker).map(this::map).toList())
    }

    private fun map(element: TidslinjeElement): HistoriskElementDto {
        return HistoriskElementDto(map(element.periode()), element.kilde(), element.type())
    }

    private fun map(periode: Periode): PeriodeDto {
        return PeriodeDto(periode.fra.toString(),
                Optional.ofNullable<LocalDate>(periode.til)
                        .map { obj: LocalDate -> obj.toString() }
                        .orElse(null))
    }
}