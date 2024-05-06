package org.examples.drools.kafka.eventprocessing;

import java.util.concurrent.TimeUnit;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.drools.ruleunits.api.DataObserver;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.api.time.SessionPseudoClock;

@Startup
@ApplicationScoped
public class Adaptor {

    @Inject
    RuleUnit<AlertingUnit> ruleUnit;

    AlertingUnit alertingUnit;
    RuleUnitInstance<AlertingUnit> ruleUnitInstance;

    @Inject
    @Channel("alerts")
    Emitter<Alert> emitter;

    @PostConstruct
    void init() {
        this.alertingUnit = new AlertingUnit();
        this.ruleUnitInstance = ruleUnit.createInstance(alertingUnit);
        alertingUnit.getAlertData().subscribe(DataObserver.of(emitter::send));
    }

    @Incoming("events")
    public void receive(Event event) throws InterruptedException {
        SessionPseudoClock sessionClock = ruleUnitInstance.getClock();

        if (event.hasTimestamp()) {
            long currentTime = sessionClock.getCurrentTime();
            if (currentTime < event.getTimestamp()) {
                sessionClock.advanceTime(event.getTimestamp() - currentTime, TimeUnit.MILLISECONDS);
            }
        } else {
            sessionClock.advanceTime(1, TimeUnit.SECONDS);
            event.setTimestamp(sessionClock.getCurrentTime());
        }

        alertingUnit.getEventData().append(event);
        ruleUnitInstance.fire();
    }
}
