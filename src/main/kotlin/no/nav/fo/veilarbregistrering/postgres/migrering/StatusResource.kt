package no.nav.fo.veilarbregistrering.postgres.migrering

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Profile("gcp")
class StatusResource(private val migrationStatusService: MigrationStatusService) {

    @GetMapping("/databases/compare")
    fun compareDatabases(): List<Tabellsjekk> = migrationStatusService.compareDatabaseStatus()
}