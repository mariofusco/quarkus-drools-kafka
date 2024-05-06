package org.examples.drools.kafka.eventprocessing;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class EventDeserializer extends ObjectMapperDeserializer<Event> {

    public EventDeserializer() {
        super(Event.class);
    }
}
