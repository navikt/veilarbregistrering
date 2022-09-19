package no.nav.fo.veilarbregistrering.migrering.tilbyder.resources

import no.nav.fo.veilarbregistrering.migrering.tilbyder.Secrets
import no.nav.fo.veilarbregistrering.migrering.tilbyder.TabellNavn
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.tilbyder.MigreringRepository
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.springframework.web.bind.annotation.*
import javax.ws.rs.ForbiddenException


@RestController
@RequestMapping("/api/migrering")
class MigreringResource(
    val migreringRepository: MigreringRepository,
    val brukerRegistreringRepository: BrukerRegistreringRepository,
    val registreringTilstandRepository: RegistreringTilstandRepository,
) {
    @GetMapping()
    fun hentNesteFraTabell(@RequestHeader("x-token") token: String, @RequestParam() tabellNavn: TabellNavn, @RequestParam() idSisthentet: Long): List<Map<String, Any>> {
        sjekkToken(token)
        return migreringRepository.nesteFraTabell(tabellNavn, idSisthentet)
    }

    @GetMapping("/status")
    fun hentStatus(@RequestHeader("x-token") token: String): List<Map<String, Any>> {
        sjekkToken(token)
        return migreringRepository.hentStatus()
    }

    @GetMapping("/registrering-tilstand/antall-potensielt-oppdaterte")
    fun hentAntallPotensieltOppdaterte(@RequestHeader("x-token") token: String): Map<String, Int> {
        sjekkToken(token)
        return mapOf("antall" to migreringRepository.hentAntallPotensieltOppdaterte())
    }

    @PostMapping("/registrering-tilstand/hent-oppdaterte-statuser")
    fun hentOppdatertStatusFor(
        @RequestHeader("x-token") token: String,
        @RequestBody sjekkDisse: Map<String, Status>): List<Map<String, Any?>> {
        sjekkToken(token)

        val tilstander =
            migreringRepository.hentRegistreringTilstander(sjekkDisse.keys.map(String::toLong))

        val results = tilstander.filterIndexed { index, rad ->
            if (index == 0) logger.info("Sammenlikner ${rad["STATUS"]} med ${sjekkDisse[rad["ID"].toString()]}")
            rad["STATUS"] != sjekkDisse[rad["ID"].toString()]
        }

        logger.info("Oppdatering av RegistreringTilstander: ${results.size} av ${sjekkDisse.size} hadde endret status")
        return results
    }

    @GetMapping("/sjekksum/{tabellnavn}")
    fun hentSjekksumForTabell(@RequestHeader("x-token") token: String, @PathVariable tabellnavn: TabellNavn): List<Map<String, Any>> {
        sjekkToken(token)
        return migreringRepository.hentSjekksumFor(tabellnavn)
    }

    private fun sjekkToken(token: String) {
        val secret = Secrets["vault/migration-token"]

        if (!secret.equals(token)) {
            throw ForbiddenException("Ugydlig token")
        }
    }
}