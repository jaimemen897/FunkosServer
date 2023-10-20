package repositories.funkos;

import enums.Modelo;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.crud.CrudRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface FunkoRepository extends CrudRepository<Funko, Long> {
    // Buscar por nombre
    Mono<Funko> save(Funko funko);

    // Actualizar
    Mono<Funko> update(Funko funko);

    // Buscar por ID
    Mono<Funko> findById(Long id);

    // Buscar por codigo
    Mono<Funko> findByCodigo(String codigo);

    // Buscar por nombre
    Flux<Funko> findByNombre(String nombre);

    // Buscar por modelo
    Flux<Funko> findByModelo(Modelo modelo);

    // Buscar por fecha de lanzamiento
    Flux<Funko> findByReleaseDate(LocalDate fecha);

    // Buscar todos
    Flux<Funko> findAll();

    // Borrar por ID
    Mono<Boolean> deleteById(Long id);

    // Borrar todos
    Mono<Void> deleteAll();
}
