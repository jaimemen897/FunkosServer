package services.funkos;

import models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunkoCacheImpl implements FunkoCache {
    private static final int MAX_SIZE = 15;
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    private final Map<Long, Funko> cache;
    private final ScheduledExecutorService cleaner;

    public FunkoCacheImpl() {
        this.cache = new LinkedHashMap<>(MAX_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Funko> eldest) {
                return size() > MAX_SIZE;
            }
        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear, 90, 90, TimeUnit.SECONDS);
    }

    @Override
    public Mono<Void> put(Long key, Funko value) {
        logger.debug("Guardando funko en la cache con id: {}", key);
        return Mono.fromRunnable(() -> cache.put(key, value));
    }

    @Override
    public Mono<Funko> get(Long key) {
        logger.debug("Obteniendo funko de la cache con id: {}", key);
        return Mono.justOrEmpty(cache.get(key));
    }

    @Override
    public Mono<Void> remove(Long key) {
        logger.debug("Eliminando funko de la cache con id: {}", key);
        return Mono.fromRunnable(() -> cache.remove(key));
    }

    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdatedAt().plusSeconds(90).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.info("Eliminando funko de la cache con id: {}", entry.getKey());
            }
            return shouldRemove;
        });
    }

    @Override
    public synchronized void shutdown() {
        logger.info("Cerrando cache");
        cleaner.shutdown();
    }
}