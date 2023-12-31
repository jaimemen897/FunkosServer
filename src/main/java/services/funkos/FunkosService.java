package services.funkos;

import enums.Modelo;
import exceptions.funko.FunkoNotFoundException;
import exceptions.funko.FunkoNotStoragedException;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public interface FunkosService {
    Flux<Funko> findAll() throws ExecutionException, InterruptedException;

    Flux<Funko> findByNombre(String nombre) throws ExecutionException, InterruptedException, FunkoNotFoundException;

    Mono<Funko> findById(long id) throws ExecutionException, InterruptedException, FunkoNotFoundException;

    Mono<Funko> findByCodigo(String codigo);

    Flux<Funko> findByReleaseDate(LocalDate localDate);

    Flux<Funko> findByModelo(Modelo modelo);

    Mono<Funko> save(Funko funko) throws ExecutionException, InterruptedException, FunkoNotStoragedException;

    Mono<Funko> update(Funko funko) throws ExecutionException, InterruptedException, FunkoNotStoragedException, FunkoNotFoundException;

    Mono<Boolean> deleteById(long id) throws ExecutionException, InterruptedException, FunkoNotFoundException;

    Mono<Void> deleteAll() throws ExecutionException, InterruptedException;
}
