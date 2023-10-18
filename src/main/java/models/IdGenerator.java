package models;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static IdGenerator instance;

    private final AtomicLong myId = new AtomicLong(1);

    public synchronized static IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    public Long getAndIncrement() {
        return myId.getAndIncrement();
    }
}