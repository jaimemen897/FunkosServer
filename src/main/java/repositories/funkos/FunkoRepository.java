package repositories.funkos;

import enums.Modelo;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.crud.CrudRepository;

import java.time.LocalDate;

public interface FunkoRepository extends CrudRepository<Funko, Long> {

    Mono<Funko> save(Funko funko);

    Mono<Funko> update(Funko funko);

    Mono<Funko> findById(Long id);

    Flux<Funko> findByNombre(String nombre);

    Mono<Funko> findByCodigo(String code);

    Flux<Funko> findByModelo(Modelo modelo);

    Flux<Funko> findByReleaseDate(LocalDate fecha);

    Flux<Funko> findAll();

    Mono<Boolean> deleteById(Long id);

    Mono<Void> deleteAll();
}
