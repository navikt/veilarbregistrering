package no.nav.fo.veilarbregistrering.db.migrering

import no.nav.fo.veilarbregistrering.Application
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.ws.rs.ForbiddenException


@RestController
@RequestMapping("/api/migrering")
class MigreringResource(
    val migreringRepositoryImpl: MigreringRepositoryImpl,
    val brukerRegistreringRepository: BrukerRegistreringRepository,
    val registreringTilstandRepository: RegistreringTilstandRepository,
) {


    @GetMapping()
    fun hentNesteFraTabell(@RequestHeader("x-token") token: String, @RequestParam() tabellNavn: TabellNavn, @RequestParam() idSisthentet: Long): List<Map<String, Any>> {
        sjekkToken(token)

        return migreringRepositoryImpl.nesteFraTabell(tabellNavn, idSisthentet)
    }

    @GetMapping("/status")
    fun hentStatus(@RequestHeader("x-token") token: String): List<Map<String, Any>> {
        sjekkToken(token)
        return migreringRepositoryImpl.hentStatus()
    }

    @GetMapping("/registrering-tilstand/antall-potensielt-oppdaterte")
    fun hentAntallPotensieltOppdaterte(@RequestHeader("x-token") token: String): Map<String, Int> {
        sjekkToken(token)
        return mapOf("antall" to migreringRepositoryImpl.hentAntallPotensieltOppdaterte())
    }

    @PostMapping("/registrering-tilstand/hent-oppdaterte-statuser")
    fun hentOppdatertStatusFor(
        @RequestHeader("x-token") token: String,
        @RequestBody sjekkDisse: Map<String, Status>): List<Map<String, Any?>> {
        sjekkToken(token)

        val tilstander =
            migreringRepositoryImpl.hentRegistreringTilstander(sjekkDisse.keys.map(String::toLong))

        val results = tilstander.filter {
            it["STATUS"] != sjekkDisse[it["ID"].toString()]
        }

        logger.info("RegistreringTilstander som er oppdatert  $results")
        return results
    }

    @GetMapping("/sjekksum/{tabellnavn}")
    fun hentSjekksumForTabell(@RequestHeader("x-token") token: String, @PathVariable tabellnavn: TabellNavn): List<Map<String, Any>> {
        sjekkToken(token)
        return migreringRepositoryImpl.hentSjekksumFor(tabellnavn)
    }

    private fun sjekkToken(token: String) {
        val secret = no.nav.fo.veilarbregistrering.config.Secrets["vault/migration-token"]

        if (!secret.equals(token)) {
            throw ForbiddenException("Ugydlig token")
        }
    }
}