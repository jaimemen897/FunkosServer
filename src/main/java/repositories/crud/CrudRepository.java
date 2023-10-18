package repositories.crud;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudRepository<T, ID> {
    // Guardar
    Mono<T> save(T t);

    // Actualizar
    Mono<T> update(T t);

    // Buscar por ID
    Mono<T> findById(ID id);

    // Buscar por nombre
    Flux<T> findByNombre(String nombre);

    // Buscar todos
    Flux<T> findAll();

    // Borrar por ID
    Mono<Boolean> deleteById(ID id);

    // Borrar todos
    Mono<Void> deleteAll();
}
