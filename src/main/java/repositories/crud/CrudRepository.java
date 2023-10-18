package repositories.crud;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudRepository<T, K> {
    Mono<T> save(T t);

    Mono<T> update(T t);

    Mono<T> findById(K id);

    Flux<T> findByNombre(String nombre);

    Flux<T> findAll();

    Mono<Boolean> deleteById(K id);

    Mono<Void> deleteAll();
}
