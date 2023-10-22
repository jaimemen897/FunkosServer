package services.funkos;

import enums.Modelo;
import models.Funko;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.funkos.FunkoRepositoryImpl;
import services.database.DataBaseManager;
import services.funkos.FunkosNotificationsImpl;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FunkosServiceImplTest2 {
    DataBaseManager dataBaseManager = DataBaseManager.getInstance();
    FunkoRepositoryImpl funkoRepository = FunkoRepositoryImpl.getInstance(dataBaseManager);
    FunkosNotificationsImpl notifications = FunkosNotificationsImpl.getInstance();
    FunkosServiceImpl funkosService = FunkosServiceImpl.getInstance(funkoRepository, notifications);


    @BeforeEach
    void setUp() {
        funkosService.importFromCsvNoNotify();
    }

    @Test
    void expensiveFunkoTest() {
        Funko funko = funkosService.expensiveFunko().block();
        assertAll(
                () -> assertNotNull(funko),
                () -> assertEquals(52.9900016784668, funko.getPrecio()),
                () -> assertEquals("Peaky Blinders Tommy", funko.getNombre())
        );
    }

    @Test
    void averagePriceTest() {
        Double averagePrice = funkosService.averagePrice().block();
        assertAll(
                () -> assertNotNull(averagePrice),
                () -> assertEquals(33.51222301059299, averagePrice)
        );
    }

    @Test
    void groupByModeloTest() {
        Map<Modelo, List<Funko>> groupByModelo = funkosService.groupByModelo().block();
        assertAll(
                () -> assertNotNull(groupByModelo),
                () -> assertEquals(4, groupByModelo.size()),
                () -> assertEquals(26, groupByModelo.get(Modelo.MARVEL).size()),
                () -> assertEquals(23, groupByModelo.get(Modelo.ANIME).size()),
                () -> assertEquals(26, groupByModelo.get(Modelo.DISNEY).size()),
                () -> assertEquals(15, groupByModelo.get(Modelo.OTROS).size())
        );
    }

    @Test
    void funkosByModeloTest() {
        Map<Modelo, Long> funkosByModelo = funkosService.funkosByModelo().block();
        assertAll(
                () -> assertNotNull(funkosByModelo),
                () -> assertEquals(4, funkosByModelo.size()),
                () -> assertEquals(26, funkosByModelo.get(Modelo.MARVEL)),
                () -> assertEquals(23, funkosByModelo.get(Modelo.ANIME)),
                () -> assertEquals(26, funkosByModelo.get(Modelo.DISNEY)),
                () -> assertEquals(15, funkosByModelo.get(Modelo.OTROS))
        );
    }

    @Test
    void funkosIn2023Test() {
        List<Funko> funkosIn2023 = funkosService.funkosIn2023().collectList().block();
        assertAll(
                () -> assertNotNull(funkosIn2023),
                () -> assertEquals(57, funkosIn2023.size()),
                () -> assertEquals(2023, funkosIn2023.get(0).getFechaLanzamiento().getYear())
        );
    }

    @Test
    void numberStitchTest() {
        Double numberStitch = funkosService.numberStitch().block();
        assertAll(
                () -> assertNotNull(numberStitch),
                () -> assertEquals(26, numberStitch)
        );
    }

    @Test
    void funkoStitchTest() {
        List<Funko> funkoStitch = funkosService.funkoStitch().collectList().block();
        assertAll(
                () -> assertNotNull(funkoStitch),
                () -> assertEquals(26, funkoStitch.size()),
                () -> assertTrue(funkoStitch.get(0).getNombre().contains("Stitch"))
        );
    }
}