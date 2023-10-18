package services.funkos;

import exceptions.Funko.FunkoNotFoundException;
import exceptions.Funko.FunkoNotStoragedException;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

public interface FunkosService {
    Flux<Funko> findAll() throws ExecutionException, InterruptedException;

    Flux<Funko> findByNombre(String nombre) throws ExecutionException, InterruptedException, FunkoNotFoundException;

    Mono<Funko> findById(long id) throws ExecutionException, InterruptedException, FunkoNotFoundException;

    Mono<Funko> save(Funko funko) throws ExecutionException, InterruptedException, FunkoNotStoragedException;

    Mono<Funko> update(Funko funko) throws ExecutionException, InterruptedException, FunkoNotStoragedException, FunkoNotFoundException;

    Mono<Boolean> deleteById(long id) throws ExecutionException, InterruptedException, FunkoNotFoundException;

    Mono<Void> deleteAll() throws ExecutionException, InterruptedException;
}
