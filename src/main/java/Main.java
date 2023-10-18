import controllers.FunkoController;
import exceptions.File.ErrorInFile;
import exceptions.File.NotFoundFile;
import repositories.funkos.FunkoRepositoryImpl;
import routes.Routes;
import services.database.DataBaseManager;
import services.funkos.FunkosNotificationsImpl;
import services.funkos.FunkosServiceImpl;

public class Main {
    public static void main(String[] args) throws NotFoundFile, ErrorInFile {
        FunkosNotificationsImpl notifications = FunkosNotificationsImpl.getInstance();
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        FunkoRepositoryImpl funkoRepository = FunkoRepositoryImpl.getInstance(dataBaseManager);
        FunkosServiceImpl funkosService = FunkosServiceImpl.getInstance(funkoRepository, notifications);
        FunkoController funkoController = FunkoController.getInstance();
        Routes routes = Routes.getInstance();

        notifications.getNotificationAsFlux().subscribe(
                notification -> {
                    switch (notification.getTipo()) {
                        case NEW -> System.out.println("🟢 Funko insertado: " + notification.getContenido());
                        case UPDATED -> System.out.println("🟠 Funko actualizado: " + notification.getContenido());
                        case DELETED -> System.out.println("🔴 Funko eliminado: " + notification.getContenido());
                    }
                },
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Obtención de funkos completada")
        );

        funkoController.loadCsv();

        funkoController.expensiveFunko().subscribe(
                funko -> System.out.println("Funko más caro: " + funko),
                error -> System.err.println("Error al obtener el funko más caro: " + error.getMessage()),
                () -> System.out.println("Obtención del funko más caro completada")
        );

        funkoController.averagePrice().subscribe(
                average -> System.out.println("Precio medio: " + average),
                error -> System.err.println("Error al obtener el precio medio: " + error.getMessage()),
                () -> System.out.println("Obtención del precio medio completada")
        );

        funkoController.groupByModelo().subscribe(
                funko -> System.out.println("Funkos agrupados por modelo: " + funko),
                error -> System.err.println("Error al obtener los funkos agrupados por modelo: " + error.getMessage()),
                () -> System.out.println("Obtención de los funkos agrupados por modelo completada")
        );

        funkoController.funkosByModelo().subscribe(
                funko -> System.out.println("Funkos agrupados por modelo: " + funko),
                error -> System.err.println("Error al obtener los funkos agrupados por modelo: " + error.getMessage()),
                () -> System.out.println("Obtención de los funkos agrupados por modelo completada")
        );

        funkoController.funkosIn2023().collectList().subscribe(
                funko -> System.out.println("Funkos que saldrán en 2023: " + funko),
                error -> System.err.println("Error al obtener los funkos que saldrán en 2023: " + error.getMessage()),
                () -> System.out.println("Obtención de los funkos que saldrán en 2023 completada")
        );

        funkoController.funkoStitch().collectList().subscribe(
                funko -> System.out.println("Funkos de Stitch: " + funko),
                error -> System.err.println("Error al obtener los funkos de Stitch: " + error.getMessage()),
                () -> System.out.println("Obtención de los funkos de Stitch completada")
        );

        funkoController.numberStitch().subscribe(
                funko -> System.out.println("Número de funkos de Stitch: " + funko),
                error -> System.err.println("Error al obtener el número de funkos de Stitch: " + error.getMessage()),
                () -> System.out.println("Obtención del número de funkos de Stitch completada")
        );

        funkosService.findById(80L).subscribe(
                funkos -> System.out.println("Funko con ID 80: " + funkos),
                error -> System.err.println("Error al obtener todos los funkos: " + error.getMessage()),
                () -> System.out.println("Obtención de funkos completada")
        );

        funkosService.findByNombre("Stitch").collectList().subscribe(
                funkos -> System.out.println("Funko con nombre Stitch: " + funkos),
                error -> System.err.println("Error al obtener todos los funkos: " + error.getMessage()),
                () -> System.out.println("Obtención de funkos completada")
        );

        funkoController.exportJson(routes.getRouteFunkosJson());
        System.exit(0);
    }
}