package services.funkos;

import adapters.FunkoAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.Modelo;
import exceptions.file.ErrorInFile;
import exceptions.file.NotFoundFile;
import models.Funko;
import models.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FunkoStorageImpl implements FunkoStorage {
    private static FunkoStorageImpl instance = null;
    private final IdGenerator idGenerator;
    private final Logger logger = LoggerFactory.getLogger(FunkoStorageImpl.class);
    private final List<Funko> funkos = new ArrayList<>();

    private FunkoStorageImpl() {
        idGenerator = new IdGenerator();
    }

    public static FunkoStorageImpl getInstance() {
        if (instance == null) {
            instance = new FunkoStorageImpl();
        }
        return instance;
    }

    @Override
    public Flux<Funko> loadCsv() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("funkos.csv");
        if (inputStream == null) {
            throw new NotFoundFile("No se ha encontrado el archivo");
        }
        return Flux.using(
                () -> new BufferedReader(new InputStreamReader(inputStream)),
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
                        throw new ErrorInFile("Error al cerrar el archivo CSV: " + e.getMessage());
                    }
                });
    }

    @Override
    public Mono<Void> exportJson(String ruta) {
        logger.info("Exportando funkos a JSON, ruta: {} ", ruta);

        return Mono.fromRunnable((() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Funko.class, new FunkoAdapter());
            Gson gson = gsonBuilder.setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter(ruta)) {
                gson.toJson(funkos, writer);
            } catch (IOException e) {
                throw new ErrorInFile("Error al escribir en el archivo JSON: " + e.getMessage());
            }
        }));
    }
}
