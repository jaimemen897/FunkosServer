package repositories.crud;

import enums.Modelo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CrudRepository<T, UUID> {
    // Guardar
    Mono<T> save(T t);

    // Actualizar
    Mono<T> update(T t);

    // Buscar por ID
    Mono<T> findByCod(UUID cod);

    //Buscar por modelo
    Flux<T> findByModel(Modelo modelo);

    //Buscar por año de lanzamiento
    Flux<T> findByFechaLanz(LocalDate fechaLanzamiento);

    // Buscar todos
    Flux<T> findAll();

    // Borrar todos
    Mono<Void> deleteAll();
}