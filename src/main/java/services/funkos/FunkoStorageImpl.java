package services.funkos;

import enums.Modelo;
import models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import routes.Routes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FunkoStorageImpl implements FunkoStorage {
    private static FunkoStorageImpl instance;
    private final Routes routes;
    private final Logger logger = LoggerFactory.getLogger(FunkoStorageImpl.class);
    private final List<Funko> funkos = new ArrayList<>();

    private FunkoStorageImpl() {
        routes = Routes.getInstance();
    }

    public static synchronized FunkoStorageImpl getInstance() {
        if (instance == null) {
            instance = new FunkoStorageImpl();
        }
        return instance;
    }

    @Override
    public Flux<Funko> loadCsv() {
        return Flux.using(
                () -> new BufferedReader(new FileReader(routes.getRouteFunkosCsv())),
                br -> Flux.fromStream(br.lines().skip(1).map(line -> {
                    String[] split = line.split(",");

                    int year = Integer.parseInt(split[4].split("-")[0]);
                    int month = Integer.parseInt(split[4].split("-")[1]);
                    int day = Integer.parseInt(split[4].split("-")[2]);

                    LocalDate dia = LocalDate.of(year, month, day);
                    UUID cod = UUID.fromString(split[0].substring(0, 35));
                    Funko funko = Funko.builder()
                            .cod(cod)
                            .nombre(split[1]).modelo(Modelo.valueOf(split[2]))
                            .precio(Double.parseDouble(split[3]))
                            .fechaLanzamiento(dia)
                            .build();
                    funkos.add(funko);
                    return funko;
                })),
                br -> {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
    }
}
