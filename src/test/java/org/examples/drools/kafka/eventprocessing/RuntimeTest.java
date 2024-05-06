package org.examples.drools.kafka.eventprocessing;

import java.util.concurrent.TimeUnit;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class RuntimeTest {

    @Inject
    @Connector(value = "smallrye-in-memory")
    InMemoryConnector connector;

    @Test
    public void sendEvents() {
        InMemorySource<Event> incomingEvents = connector.source("events");
        InMemorySink<Alert> outgoingAlerts = connector.sink("alerts");

        incomingEvents.send(new Event("temperature", 35, 0, TimeUnit.SECONDS));
        incomingEvents.send(new Event("temperature", 20, 3, TimeUnit.SECONDS)); // under threshold temperature
        incomingEvents.send(new Event("temperature", 37, 6, TimeUnit.SECONDS)); // increase out of the 5s allowed time window
        incomingEvents.send(new Event("temperature", 41, 9, TimeUnit.SECONDS)); // actual temperature increase event

        assertThat(outgoingAlerts.received().size()).isEqualTo(1);
        Alert alert = outgoingAlerts.received().get(0).getPayload();
        assertThat(alert.getSeverity()).isEqualTo("warning");
        assertThat(alert.getMessage()).isEqualTo("Temperature increase: 4");

    }
}
