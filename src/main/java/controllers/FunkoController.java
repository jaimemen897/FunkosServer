package controllers;

import enums.Modelo;
import lombok.Getter;
import models.Funko;
import models.IdGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.funkos.FunkoRepositoryImpl;
import routes.Routes;
import services.database.DataBaseManager;
import services.funkos.FunkosNotifications;
import services.funkos.FunkosNotificationsImpl;
import services.funkos.FunkosServiceImpl;

import java.util.List;
import java.util.Map;

@Getter
public class FunkoController {
    private static final FunkoController instance = new FunkoController();
    private final IdGenerator idGenerator;
    private final Routes routes;
    private final FunkoRepositoryImpl funkoRepository;
    private final FunkosNotifications notification;
    private final FunkosServiceImpl funkosService;

    private FunkoController() {
        idGenerator = IdGenerator.getInstance();
        routes = Routes.getInstance();
        funkoRepository = FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance());
        notification = FunkosNotificationsImpl.getInstance();
        funkosService = FunkosServiceImpl.getInstance(funkoRepository, notification);
    }

    public static synchronized FunkoController getInstance() {
        return instance;
    }

    public void loadCsv() {
        funkosService.importFromCsv();
    }

    public void exportJson(String ruta) {
        funkosService.exportToJson(ruta);
    }

    public Mono<Funko> expensiveFunko() {
        return funkosService.expensiveFunko();
    }

    public Mono<Double> averagePrice() {
        return funkosService.averagePrice();
    }

    public Mono<Map<Modelo, List<Funko>>> groupByModelo() {
        return funkosService.groupByModelo();
    }

    public Mono<Map<Modelo, Long>> funkosByModelo() {
        return funkosService.funkosByModelo();
    }

    public Flux<Funko> funkosIn2023() {
        return funkosService.funkosIn2023();
    }

    public Mono<Double> numberStitch() {
        return funkosService.numberStitch();
    }

    public Flux<Funko> funkoStitch() {
        return funkosService.funkoStitch();
    }
}
