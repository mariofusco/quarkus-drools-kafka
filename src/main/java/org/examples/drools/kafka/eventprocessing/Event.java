package org.examples.drools.kafka.eventprocessing;

import java.util.concurrent.TimeUnit;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("10s")
public class Event {

    private String type;
    private int value;
    private long timestamp = -1;

    public Event() {
    }

    public Event(String type, int value, int timestamp, TimeUnit timeUnit) {
        this.type = type;
        this.value = value;
        this.timestamp = timeUnit.toMillis(timestamp);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean hasTimestamp() {
        return timestamp >= 0;
    }

    @Override
    public String toString() {
        return "Event [type=" + type + ", value=" + value + "]";
    }

}
