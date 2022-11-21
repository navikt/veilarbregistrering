package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.resources

import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.ArbeidssokerService
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.autorisasjon.TilgangskontrollService
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.bruker.UserService
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/arbeidssoker")
class ArbeidssokerResource(
    private val arbeidssokerService: ArbeidssokerService,
    private val userService: UserService,
    private val tilgangskontrollService: TilgangskontrollService
) : ArbeidssokerApi {
    @GetMapping("/perioder/niva3")
    override fun hentArbeidssokerperioderMedNivå3(
        @RequestParam("fraOgMed") @DateTimeFormat(pattern = "yyyy-MM-dd") fraOgMed: LocalDate,
        @RequestParam(value = "tilOgMed", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") tilOgMed: LocalDate?
    ): ArbeidssokerperioderDto {
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBrukerMedNivå3(bruker)
        return hentArbeidssokerperioder(bruker, fraOgMed, tilOgMed)
    }

    @GetMapping("/perioder")
    override fun hentArbeidssokerperioder(
        @RequestParam("fnr") fnr: String,
        @RequestParam("fraOgMed") @DateTimeFormat(pattern = "yyyy-MM-dd") fraOgMed: LocalDate,
        @RequestParam(value = "tilOgMed", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") tilOgMed: LocalDate?
    ): ArbeidssokerperioderDto {
        logger.info("hentArbeidssokerperioder med fnr i request param - deprecated metode")
        val bruker = userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        return hentArbeidssokerperioder(bruker, fraOgMed, tilOgMed)
    }

    @PostMapping("/perioder")
    override fun hentArbeidssokerperioder(
        @RequestBody fnr: Fnr?,
        @RequestParam("fraOgMed") @DateTimeFormat(pattern = "yyyy-MM-dd") fraOgMed: LocalDate,
        @RequestParam(value = "tilOgMed", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") tilOgMed: LocalDate?
    ): ArbeidssokerperioderDto {
        logger.info("hentArbeidssokerperioder med fnr i body")
        val bruker = if (fnr != null) userService.finnBrukerGjennomPdl(Foedselsnummer(fnr.fnr)) else userService.finnBrukerGjennomPdl()
        tilgangskontrollService.sjekkLesetilgangTilBruker(bruker.gjeldendeFoedselsnummer)
        return hentArbeidssokerperioder(bruker, fraOgMed, tilOgMed)
    }

    private fun hentArbeidssokerperioder(bruker: Bruker,
                                         fraOgMed: LocalDate,
                                         tilOgMed: LocalDate?): ArbeidssokerperioderDto {

        val arbeidssokerperiodes = arbeidssokerService.hentArbeidssokerperioder(
            bruker, Periode.gyldigPeriode(fraOgMed, tilOgMed)
        )
        LOG.info("Ferdig med henting av arbeidssokerperioder - fant ${arbeidssokerperiodes.asList().size} perioder")
        return map(arbeidssokerperiodes.eldsteFoerst())
    }

    private fun map(arbeidssokerperioder: List<Arbeidssokerperiode>): ArbeidssokerperioderDto {
        val arbeidssokerperiodeDtoer = arbeidssokerperioder
            .map { periode: Arbeidssokerperiode ->
                ArbeidssokerperiodeDto(
                    periode.periode.fra.toString(),
                    periode.periode.til?.toString(),
                )
            }

        return ArbeidssokerperioderDto(arbeidssokerperiodeDtoer)
    }

    companion object {
        private val LOG = loggerFor<ArbeidssokerResource>()
    }
}

data class Fnr(val fnr: String)
