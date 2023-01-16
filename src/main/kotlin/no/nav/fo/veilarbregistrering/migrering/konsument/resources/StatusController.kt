package no.nav.fo.veilarbregistrering.migrering.konsument.resources

import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrationStatusService
import no.nav.fo.veilarbregistrering.migrering.konsument.Tabellsjekk
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class StatusController(private val migrationStatusService: MigrationStatusService) {

    @GetMapping("/compareDatabases")
    fun compareDatabases(): List<Tabellsjekk> {
        if (isOnPrem()) throw IllegalStateException("compareDatabases for GCP ble forsøkt kjørt fra FSS")
        return migrationStatusService.compareDatabaseStatus()
    }

    @GetMapping("/compare-profilering")
    fun finnManglendeProfilering(): List<Long> {
        if (isOnPrem()) throw IllegalStateException("compareDatabases for GCP ble forsøkt kjørt fra FSS")
        return migrationStatusService.compareProfilering()
    }
}