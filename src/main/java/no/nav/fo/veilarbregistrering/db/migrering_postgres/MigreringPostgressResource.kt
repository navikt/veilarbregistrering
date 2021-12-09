package no.nav.fo.veilarbregistrering.db.migrering_postgres

import no.nav.fo.veilarbregistrering.Application
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.ws.rs.ForbiddenException


@RestController
@RequestMapping("/api/migrering")
class MigreringPostgressResource(
                                 val migreringRepositoryImpl: MigreringRepositoryImpl,
                                 val brukerRegistreringRepository: BrukerRegistreringRepository
                                 ) {
    companion object {
        private fun getVaultSecret(path: String): String? {
            return try {
                String(Files.readAllBytes(Paths.get(Application.SECRETS_PATH, path)), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                throw IllegalStateException(String.format("Klarte ikke laste property fra vault for path: %s", path), e)
            }
        }
    }

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

    @GetMapping("/sjekksum/{tabellnavn}")
    fun hentSjekksumForTabell(@RequestHeader("x-token") token: String, @PathVariable tabellnavn: TabellNavn): List<Map<String, Any>> {
        sjekkToken(token)
        return migreringRepositoryImpl.hentSjekksumFor(tabellnavn)
    }

    private fun sjekkToken(token: String) {
        val secret = getVaultSecret("vault/migration-token")

        if (!secret.equals(token)) {
            throw ForbiddenException("Ugydlig token")
        }
    }
}