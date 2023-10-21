package models;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final IdGenerator instance = new IdGenerator();
    private final AtomicLong myId = new AtomicLong(1);

    public static synchronized IdGenerator getInstance() {
        return instance;
    }

    public Long getAndIncrement() {
        return myId.getAndIncrement();
    }
}