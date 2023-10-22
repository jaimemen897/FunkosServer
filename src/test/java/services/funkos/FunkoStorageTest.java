package services.funkos;

import models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunkoStorageTest {
    FunkoStorageImpl funkoStorage;

    @BeforeEach
    void setUp() {
        funkoStorage = FunkoStorageImpl.getInstance();
    }

    @Test
    void loadCsv() {
        List<Funko> funkos = funkoStorage.loadCsv().collectList().block();
        assertFalse(funkos.isEmpty());
    }

    @Test
    void exportJson() {
        funkoStorage.exportJson("src" + File.separator + "data" + File.separator + "funkos.json");
        assertTrue(Files.exists(Paths.get("src" + File.separator + "data" + File.separator + "funkos.json")));
    }
}