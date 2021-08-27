package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.TriggerContext
import java.util.*

class TaskTrigger(private val leaderElectionClient: LeaderElectionClient, private val publiseringAvEventsService: PubliseringAvEventsService) : Trigger {

    override fun nextExecutionTime(triggerContext: TriggerContext): Date? {
        val nextExecutionTime: Calendar = GregorianCalendar()
        val lastActualExecutionTime = triggerContext.lastActualExecutionTime()
        nextExecutionTime.time = lastActualExecutionTime ?: Date()
        val delay = calculateDelay(triggerContext)
        nextExecutionTime.add(Calendar.MILLISECOND, delay)
        return nextExecutionTime.time
    }

    private fun calculateDelay(triggerContext: TriggerContext): Int {
        val rate = when {
            !leaderElectionClient.isLeader -> NON_LEADER_RATE
            !publiseringAvEventsService.harVentendeEvents() -> QUEUE_EMPTY_RATE
            else -> QUICKEST_RATE
        }
        val executionTime = triggerContext.lastActualExecutionTime()
        val completionTime = triggerContext.lastCompletionTime()
        if (executionTime != null && completionTime != null) {
            return (completionTime.time - executionTime.time + rate).toInt()
        }
        return rate
    }

    companion object {
        private const val NON_LEADER_RATE = 20000
        private const val QUEUE_EMPTY_RATE = 10000
        private const val QUICKEST_RATE = 1000
    }
}