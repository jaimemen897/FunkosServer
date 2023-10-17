package repositories.funkos;

import enums.Modelo;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import services.database.DataBaseManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FunkoRepositoryImpl implements FunkoRepository {
    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);
    private final ConnectionPool connectionFactory;

    private FunkoRepositoryImpl(DataBaseManager db) {
        this.connectionFactory = db.getConnectionPool();
    }

    public static synchronized FunkoRepositoryImpl getInstance(DataBaseManager db) {
        if (instance == null) {
            instance = new FunkoRepositoryImpl(db);
        }
        return instance;
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Insertando funko: " + funko);
        String query = "INSERT INTO FUNKOS (cod, nombre, modelo, precio, fechaLanzamiento) VALUES (?, ?, ?, ?, ?)";

        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getCod().toString())
                        .bind(1, funko.getNombre())
                        .bind(2, funko.getModelo().toString())
                        .bind(3, funko.getPrecio())
                        .bind(4, funko.getFechaLanzamiento())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        String query = "UPDATE FUNKOS SET nombre = ?, modelo = ?, precio = ?, fechaLanzamiento = ? WHERE cod = ?";
        funko.setUpdatedAt(LocalDateTime.now());
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getNombre())
                        .bind(1, funko.getModelo().toString())
                        .bind(2, funko.getPrecio())
                        .bind(3, funko.getFechaLanzamiento())
                        .bind(4, funko.getCod())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );

    }

    @Override
    public Mono<Funko> findByCod(UUID cod) {
        logger.debug("Buscando funko por codigo: " + cod);
        String query = "SELECT * FROM FUNKOS WHERE cod = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, cod)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((fila, datos) ->
                        Funko.builder()
                                .cod(UUID.fromString(fila.get("cod", String.class)))
                                .nombre(fila.get("nombre", String.class))
                                .modelo(Modelo.valueOf(fila.get("modelo", String.class)))
                                .precio(fila.get("precio", Float.class).doubleValue())
                                .fechaLanzamiento(fila.get("fechaLanzamiento", java.time.LocalDate.class))
                                .build()
                ))),
                Connection::close
        );
    }

    @Override
    public Flux<Funko> findByModel(Modelo modelo) {
        logger.debug("Buscando funko por modelo: " + modelo);
        String query = "SELECT * FROM FUNKOS WHERE modelo LIKE ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(query)
                        .bind(0, modelo.toString())
                        .execute()
                ).flatMap(result -> result.map((fila, datos) ->
                        Funko.builder()
                                .cod(UUID.fromString(fila.get("cod", String.class)))
                                .nombre(fila.get("nombre", String.class))
                                .modelo(Modelo.valueOf(fila.get("modelo", String.class)))
                                .precio(fila.get("precio", Float.class).doubleValue())
                                .fechaLanzamiento(fila.get("fechaLanzamiento", java.time.LocalDate.class))
                                .build()
                )),
                Connection::close
        );
    }

    @Override
    public Flux<Funko> findByFechaLanz(LocalDate fechaLanzamiento) {
        logger.debug("Buscando funko por fecha de lanzamiento: " + fechaLanzamiento);
        String query = "SELECT * FROM FUNKOS WHERE fechaLanzamiento = ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, fechaLanzamiento)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((fila, datos) ->
                        Funko.builder()
                                .cod(UUID.fromString(fila.get("cod", String.class)))
                                .nombre(fila.get("nombre", String.class))
                                .modelo(Modelo.valueOf(fila.get("modelo", String.class)))
                                .precio(fila.get("precio", Float.class).doubleValue())
                                .fechaLanzamiento(fila.get("fechaLanzamiento", java.time.LocalDate.class))
                                .build()
                ))),
                Connection::close
        );
    }

    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los funkos");
        String query = "SELECT * FROM FUNKOS";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(query)
                        .execute()
                ).flatMap(result -> result.map((fila, datos) ->
                        Funko.builder()
                                .cod(UUID.fromString(fila.get("cod", String.class)))
                                .nombre(fila.get("nombre", String.class))
                                .modelo(Modelo.valueOf(fila.get("modelo", String.class)))
                                .precio(fila.get("precio", Float.class).doubleValue())
                                .fechaLanzamiento(fila.get("fechaLanzamiento", LocalDate.class))
                                .build()
                )),
                Connection::close
        );
    }


    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Borrando todos los funkos");
        String query = "DELETE FROM FUNKOS";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .execute()
                ).then(),
                Connection::close
        );
    }

}
