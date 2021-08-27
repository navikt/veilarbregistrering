package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
@EnableScheduling
class DynamicPubliseringAvRegistreringEventsScheduler(
    private val publiseringAvEventsService: PubliseringAvEventsService,
    private val leaderElectionClient: LeaderElectionClient,
) : SchedulingConfigurer, Runnable {

    private val taskTrigger: TaskTrigger by lazy { TaskTrigger(leaderElectionClient, publiseringAvEventsService) }

    @Bean
    fun taskExecutor(): Executor {
        return Executors.newSingleThreadScheduledExecutor()
    }

    override fun run() {
        val startTime = System.currentTimeMillis()
        if (!leaderElectionClient.isLeader) return
        //perform
        val taskDuration = System.currentTimeMillis() - startTime
        LOG.info("Dynamic task triggered on leader took {}ms", taskDuration)
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor())
        taskRegistrar.addTriggerTask(
            this,
            taskTrigger,
        )
    }

    companion object {
        private val LOG = loggerFor<DynamicPubliseringAvRegistreringEventsScheduler>()
    }
}