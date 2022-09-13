package no.nav.fo.veilarbregistrering.helsesjekk

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestUtils
import no.nav.fo.veilarbregistrering.log.logger
import java.util.function.Supplier

class SelfTestStatusMeterBinder(private val selfTestChecks: SelfTestChecks): MeterBinder {
    override fun bindTo(registry: MeterRegistry) {
        logger.info("Pinger baksystemer og registrerer metrikk")
        selfTestChecks.selfTestChecks.map { selfTestCheck ->
            Gauge.builder("selftest_status", selfTestChecker(selfTestCheck)).tag("id", selfTestCheck.description)
        }.forEach { it.register(registry) }
    }

    fun selfTestChecker(selfTestCheck: SelfTestCheck) = Supplier<Number> {
        val result = SelfTestUtils.performSelftTestCheck(selfTestCheck).checkResult
        val status = when {
            result.isUnhealthy && selfTestCheck.isCritical -> CheckStatus.ERROR
            result.isUnhealthy && !selfTestCheck.isCritical -> CheckStatus.WARNING
            else -> CheckStatus.OK
        }
        status.statusCode
    }

    private enum class CheckStatus(val statusCode: Int) {
        OK(0),
        WARNING(2),
        ERROR(1)
    }
}
