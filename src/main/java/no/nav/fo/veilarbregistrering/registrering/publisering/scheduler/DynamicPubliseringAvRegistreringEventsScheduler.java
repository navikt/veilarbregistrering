package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;


import no.nav.common.featuretoggle.UnleashClient;
import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class DynamicPubliseringAvRegistreringEventsScheduler implements SchedulingConfigurer, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPubliseringAvRegistreringEventsScheduler.class);

    private final PubliseringAvEventsService publiseringAvEventsService;
    private final LeaderElectionClient leaderElectionClient;
    private final UnleashClient unleashClient;


    public DynamicPubliseringAvRegistreringEventsScheduler(PubliseringAvEventsService publiseringAvEventsService, LeaderElectionClient leaderElectionClient, UnleashClient unleashClient) {
        this.publiseringAvEventsService = publiseringAvEventsService;
        this.leaderElectionClient = leaderElectionClient;
        this.unleashClient = unleashClient;
    }


    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        LOG.info("Task executed");
    }

    @Override
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        if (!leaderElectionClient.isLeader()) {
            LOG.info("I am not the leader, not scheduling task");
            return;
        }
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                this,
                triggerContext -> {
                    //boolean ventendeEvents = publiseringAvEventsService.ventendeEvents() != 0;
                    boolean ventendeEvents = (int)(Math.random() * 10) % 2 == 0;
                    int rate = ventendeEvents ? 1000 : 10000;
                    LOG.info("Next run calculated to {} ms from now", rate);
                    Calendar nextExecutionTime =  new GregorianCalendar();
                    Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                    nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                    nextExecutionTime.add(Calendar.MILLISECOND, rate);
                    return nextExecutionTime.getTime();
                }
        );
    }
}
