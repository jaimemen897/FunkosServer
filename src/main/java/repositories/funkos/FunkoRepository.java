package repositories.funkos;

import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.crud.CrudRepository;

public interface FunkoRepository extends CrudRepository<Funko, Long> {
    // Buscar por nombre
    Mono<Funko> save(Funko funko);

    // Actualizar
    Mono<Funko> update(Funko funko);

    // Buscar por ID
    Mono<Funko> findById(Long id);

    // Buscar por nombre
    Flux<Funko> findByNombre(String nombre);

    // Buscar todos
    Flux<Funko> findAll();

    // Borrar por ID
    Mono<Boolean> deleteById(Long id);

    // Borrar todos
    Mono<Void> deleteAll();
}
