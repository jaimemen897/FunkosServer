package models;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private final AtomicLong myId = new AtomicLong(1);
    public Long getAndIncrement() {
        return myId.getAndIncrement();
    }
}