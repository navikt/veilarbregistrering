package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.log.CallId
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TriggerContext
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
@EnableScheduling
class DynamicPubliseringAvRegistreringEventsScheduler(
    private val publiseringAvEventsService: PubliseringAvEventsService,
    private val leaderElectionClient: LeaderElectionClient,
    private val unleashClient: UnleashClient,
) : SchedulingConfigurer, Runnable {
    @Bean
    fun taskExecutor(): Executor {
        return Executors.newSingleThreadScheduledExecutor()
    }

    override fun run() {
        println("Task executed")
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        if (!leaderElectionClient.isLeader) {
            LOG.info("I am not the leader, not scheduling task")
            return
        }
        taskRegistrar.setScheduler(taskExecutor())
        taskRegistrar.addTriggerTask(
            this
        ) { triggerContext: TriggerContext ->
            val rate = if (publiseringAvEventsService.harVentendeEvents()) 1000 else 10000
            LOG.info("Next run set to {}s from now", rate/1000)
            val nextExecutionTime: Calendar = GregorianCalendar()
            val lastActualExecutionTime = triggerContext.lastActualExecutionTime()
            nextExecutionTime.time = lastActualExecutionTime ?: Date()
            nextExecutionTime.add(Calendar.MILLISECOND, rate)
            nextExecutionTime.time
        }
    }

    companion object {
        private val LOG = loggerFor<DynamicPubliseringAvRegistreringEventsScheduler>()
    }
}