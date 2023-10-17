package repositories.funkos;

import enums.Modelo;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.crud.CrudRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface FunkoRepository extends CrudRepository<Funko, UUID> {
    // Guardar
    Mono<Funko> save(Funko funko);

    // Actualizar
    Mono<Funko> update(Funko funko);

    // Buscar por ID
    Mono<Funko> findByCod(UUID cod);

    //Buscar por modelo
    Flux<Funko> findByModel(Modelo modelo);

    //Buscar por año de lanzamiento
    Flux<Funko> findByFechaLanz(LocalDate fechaLanzamiento);

    // Buscar todos
    Flux<Funko> findAll();

    // Borrar todos
    Mono<Void> deleteAll();
}

