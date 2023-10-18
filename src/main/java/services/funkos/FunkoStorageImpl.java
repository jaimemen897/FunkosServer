package services.funkos;

import adapters.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.Modelo;
import exceptions.File.ErrorInFile;
import exceptions.File.NotFoundFile;
import models.Funko;
import models.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.funkos.FunkoRepositoryImpl;
import routes.Routes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FunkoStorageImpl implements FunkoStorage {
    private static FunkoStorageImpl instance;
    private final IdGenerator idGenerator;
    private final Routes routes;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);
    private final List<Funko> funkos = new ArrayList<>();

    private FunkoStorageImpl() {
        idGenerator = IdGenerator.getInstance();
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
                            .id2(idGenerator.getAndIncrement())
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
                        throw new NotFoundFile("No se ha encontrado el archivo");
                    }
                });
    }

    @Override
    public Mono<Void> exportJson(String ruta) {
        logger.debug("Exportando funkos a JSON, ruta: " + ruta);

        return Mono.fromRunnable((() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Funko.class, new LocalDateAdapter());
            Gson gson = gsonBuilder.setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter(ruta)) {
                gson.toJson(funkos, writer);
            } catch (IOException e) {
                throw new ErrorInFile("Error al escribir en el archivo JSON: " + e.getMessage());
            }
        }));
    }
}
