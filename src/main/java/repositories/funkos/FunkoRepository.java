package repositories.funkos;

import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.crud.CrudRepository;

public interface FunkoRepository extends CrudRepository<Funko, Long> {

    Mono<Funko> save(Funko funko);

    Mono<Funko> update(Funko funko);

    Mono<Funko> findById(Long id);

    Flux<Funko> findByNombre(String nombre);

    Flux<Funko> findAll();

    Mono<Boolean> deleteById(Long id);

    Mono<Void> deleteAll();
}
