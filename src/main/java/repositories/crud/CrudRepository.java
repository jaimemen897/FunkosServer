package repositories.crud;

import enums.Modelo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface CrudRepository<T, ID> {
    // Guardar
    Mono<T> save(T t);

    // Actualizar
    Mono<T> update(T t);

    // Buscar por ID
    Mono<T> findById(ID id);

    // Buscar por codigo
    Mono<T> findByCodigo(UUID codigo);

    // Buscar por nombre
    Flux<T> findByNombre(String nombre);

    // Buscar por modelo
    Flux<T> findByModelo(Modelo modelo);

    // Buscar por fecha de lanzamiento
    Flux<T> findByReleaseDate(LocalDate fecha);

    // Buscar todos
    Flux<T> findAll();

    // Borrar por ID
    Mono<Boolean> deleteById(ID id);

    // Borrar todos
    Mono<Void> deleteAll();
}
