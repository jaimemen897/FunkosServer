package services.funkos;

import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FunkoStorage {
    Flux<Funko> loadCsv();

    Mono<Void> exportJson(String ruta);
}
