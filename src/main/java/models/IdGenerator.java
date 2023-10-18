package models;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final IdGenerator instance = new IdGenerator();

    public static synchronized IdGenerator getInstance() {
        return instance;
    }

    private final AtomicLong myId = new AtomicLong(1);

    public Long getAndIncrement() {
        return myId.getAndIncrement();
    }
}