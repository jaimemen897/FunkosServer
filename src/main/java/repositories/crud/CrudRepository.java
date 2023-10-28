package repositories.crud;

import enums.Modelo;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CrudRepository<T, K> {
    Mono<T> save(T t);

    Mono<T> update(T t);

    Mono<T> findById(K id);

    Flux<T> findByNombre(String nombre);

    Mono<Funko> findByCodigo(String code);

    Flux<Funko> findByModelo(Modelo modelo);

    Flux<Funko> findByReleaseDate(LocalDate fecha);

    Flux<T> findAll();

    Mono<Boolean> deleteById(K id);

    Mono<Void> deleteAll();
}
