package no.nav.fo.veilarbregistrering.helsesjekk.resources

import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestUtils
import no.nav.common.health.selftest.SelfTestUtils.checkAll
import no.nav.common.health.selftest.SelfTestUtils.checkAllParallel
import no.nav.common.health.selftest.SelftTestCheckResult
import no.nav.common.health.selftest.SelftestHtmlGenerator
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/internal")
class HelsesjekkResource(@Autowired private val selfTestChecks: SelfTestChecks) {

    @GetMapping("/isAlive")
    fun isAlive() {
    }

    @GetMapping("/isReady")
    fun isReady() {
        checkAll(selfTestChecks.selfTestChecks)
            .filter { it.selfTestCheck.isCritical }
            .all { it.checkResult.isHealthy }
    }

    @GetMapping("/isReadyGcp")
    fun isReadyGcp(): ResponseEntity<Any> {
        val healthCheckOk = checkAllParallel(selfTestChecks.selfTestChecks.filter { it.isCritical })
            .filter { it.selfTestCheck.isCritical }
            .all { it.checkResult.isHealthy }
        return if (healthCheckOk) {
            logger.info("Helsesjekk OK")
            ResponseEntity.ok().build()
        } else {
            logger.info("Feil i helsesjekk")
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
        }
    }

    @GetMapping("/selftest")
    fun selfTest(): ResponseEntity<String> {
        val checkResults: List<SelftTestCheckResult> = checkAll(selfTestChecks.selfTestChecks)
        val html = SelftestHtmlGenerator.generate(checkResults)
        val status = SelfTestUtils.findHttpStatusCode(checkResults, true)

        return ResponseEntity
            .status(status)
            .contentType(MediaType.TEXT_HTML)
            .body(html)
    }

}