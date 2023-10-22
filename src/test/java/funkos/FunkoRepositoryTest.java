package funkos;

import enums.Modelo;
import models.Funko;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.funkos.FunkoRepository;
import repositories.funkos.FunkoRepositoryImpl;
import services.database.DataBaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunkoRepositoryTest {
    private FunkoRepository funkoRepository;

    @BeforeEach
    void setUp() throws SQLException {
        funkoRepository = FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance());
        funkoRepository.deleteAll().block();
    }

    @AfterEach
    void tearDown() throws SQLException {
        funkoRepository.deleteAll().block();
    }


    @Test
    void save() throws SQLException {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko savedFunko = funkoRepository.save(funko).block();
        assertAll(
                () -> assertNotNull(savedFunko),
                () -> assertEquals(funko.getModelo(), savedFunko.getModelo()),
                () -> assertEquals(funko.getNombre(), savedFunko.getNombre()),
                () -> assertEquals(funko.getFechaLanzamiento(), savedFunko.getFechaLanzamiento()),
                () -> assertEquals(funko.getPrecio(), savedFunko.getPrecio()),
                () -> assertEquals(funko.getId2(), savedFunko.getId2()),
                () -> assertEquals(funko.getCod(), savedFunko.getCod())
        );
    }

    @Test
    void update() throws SQLException {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko savedFunko = funkoRepository.save(funko).block();
        savedFunko.setNombre("Rayo McQueen 2");
        savedFunko.setPrecio(200.0);
        Funko updatedFunko = funkoRepository.update(savedFunko).block();
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.getId2()).blockOptional();
        assertAll(
                () -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(savedFunko.getModelo(), updatedFunko.getModelo()),
                () -> assertEquals(savedFunko.getNombre(), updatedFunko.getNombre()),
                () -> assertEquals(savedFunko.getFechaLanzamiento(), updatedFunko.getFechaLanzamiento()),
                () -> assertEquals(savedFunko.getPrecio(), updatedFunko.getPrecio()),
                () -> assertEquals(savedFunko.getId2(), updatedFunko.getId2()),
                () -> assertEquals(savedFunko.getCod(), updatedFunko.getCod())
        );
    }

    @Test
    void findById() throws SQLException {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko savedFunko = funkoRepository.save(funko).block();
        assert savedFunko != null;
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.getId2()).blockOptional();
        assertAll(
                () -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(savedFunko.getModelo(), foundFunko.get().getModelo()),
                () -> assertEquals(savedFunko.getNombre(), foundFunko.get().getNombre()),
                () -> assertEquals(savedFunko.getFechaLanzamiento(), foundFunko.get().getFechaLanzamiento()),
                () -> assertEquals(savedFunko.getPrecio(), foundFunko.get().getPrecio()),
                () -> assertEquals(savedFunko.getId2(), foundFunko.get().getId2()),
                () -> assertEquals(savedFunko.getCod(), foundFunko.get().getCod())
        );
    }

    @Test
    void findByIdNoExiste() throws SQLException {
        Optional<Funko> foundFunko = funkoRepository.findById(1L).blockOptional();
        assertFalse(foundFunko.isPresent());
    }

    @Test
    void findBycodigo() throws SQLException {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(99L)
                .nombre("Simba")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2023-10-22"))
                .build();
        Funko savedFunko = funkoRepository.save(funko).block();
        assert savedFunko != null;
        Optional<Funko> foundFunko = funkoRepository.findByCodigo(savedFunko.getCod().toString()).blockOptional();
        assertAll(
                () -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(savedFunko.getModelo(), foundFunko.get().getModelo()),
                () -> assertEquals(savedFunko.getNombre(), foundFunko.get().getNombre()),
                () -> assertEquals(savedFunko.getFechaLanzamiento(), foundFunko.get().getFechaLanzamiento()),
                () -> assertEquals(savedFunko.getPrecio(), foundFunko.get().getPrecio()),
                () -> assertEquals(savedFunko.getId2(), foundFunko.get().getId2()),
                () -> assertEquals(savedFunko.getCod(), foundFunko.get().getCod())
        );
    }

    @Test
    void findBycodigoNoExiste() throws SQLException {
        Optional<Funko> foundFunko = funkoRepository.findByCodigo(UUID.randomUUID().toString()).blockOptional();
        assertFalse(foundFunko.isPresent());
    }

    @Test
    void findByModelo() throws SQLException {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(99L)
                .nombre("Iron Man")
                .modelo(Modelo.MARVEL)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2023-10-22"))
                .build();
        Funko savedFunko = funkoRepository.save(funko).block();
        assert savedFunko != null;
        List<Funko> foundFunko = funkoRepository.findByModelo(savedFunko.getModelo()).collectList().block();
        assertAll(
                () -> assertNotNull(foundFunko),
                () -> assertEquals(1, foundFunko.size()),
                () -> assertEquals(savedFunko.getModelo(), foundFunko.get(0).getModelo()),
                () -> assertEquals(savedFunko.getNombre(), foundFunko.get(0).getNombre()),
                () -> assertEquals(savedFunko.getFechaLanzamiento(), foundFunko.get(0).getFechaLanzamiento()),
                () -> assertEquals(savedFunko.getPrecio(), foundFunko.get(0).getPrecio()),
                () -> assertEquals(savedFunko.getId2(), foundFunko.get(0).getId2()),
                () -> assertEquals(savedFunko.getCod(), foundFunko.get(0).getCod())
        );
    }

    @Test
    void findByModeloNoExiste() throws SQLException {
        List<Funko> foundFunko = funkoRepository.findByModelo(Modelo.MARVEL).collectList().block();
        assertAll(
                () -> assertNotNull(foundFunko),
                () -> assertEquals(0, foundFunko.size())
        );
    }

    @Test
    void findByReleaseDate() {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(99L)
                .nombre("Iron Man")
                .modelo(Modelo.MARVEL)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2023-10-22"))
                .build();
        Funko savedFunko = funkoRepository.save(funko).block();
        assert savedFunko != null;
        List<Funko> foundFunko = funkoRepository.findByReleaseDate(savedFunko.getFechaLanzamiento()).collectList().block();
        assertAll(
                () -> assertNotNull(foundFunko),
                () -> assertEquals(1, foundFunko.size()),
                () -> assertEquals(savedFunko.getModelo(), foundFunko.get(0).getModelo()),
                () -> assertEquals(savedFunko.getNombre(), foundFunko.get(0).getNombre()),
                () -> assertEquals(savedFunko.getFechaLanzamiento(), foundFunko.get(0).getFechaLanzamiento()),
                () -> assertEquals(savedFunko.getPrecio(), foundFunko.get(0).getPrecio()),
                () -> assertEquals(savedFunko.getId2(), foundFunko.get(0).getId2()),
                () -> assertEquals(savedFunko.getCod(), foundFunko.get(0).getCod())
        );
    }

    @Test
    void findByReleaseDateNoExiste() throws SQLException {
        List<Funko> foundFunko = funkoRepository.findByReleaseDate(LocalDate.parse("2023-10-22")).collectList().block();
        assertAll(
                () -> assertNotNull(foundFunko),
                () -> assertEquals(0, foundFunko.size())
        );
    }

    @Test
    void findAll() throws SQLException {
        Funko funko1 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko funko2 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(96L)
                .nombre("Mate")
                .modelo(Modelo.DISNEY)
                .precio(200.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        funkoRepository.save(funko1).block();
        funkoRepository.save(funko2).block();
        List<Funko> foundFunkos = funkoRepository.findAll().collectList().block();
        assertAll(() -> assertNotNull(foundFunkos),
                () -> assertEquals(2, foundFunkos.size()),
                () -> assertEquals(foundFunkos.get(0).getModelo(), funko1.getModelo()),
                () -> assertEquals(foundFunkos.get(0).getNombre(), funko1.getNombre()),
                () -> assertEquals(foundFunkos.get(0).getFechaLanzamiento(), funko1.getFechaLanzamiento()),
                () -> assertEquals(foundFunkos.get(0).getPrecio(), funko1.getPrecio()),
                () -> assertEquals(foundFunkos.get(0).getId2(), funko1.getId2()),
                () -> assertEquals(foundFunkos.get(0).getCod(), funko1.getCod()),
                () -> assertEquals(foundFunkos.get(1).getModelo(), funko2.getModelo()),
                () -> assertEquals(foundFunkos.get(1).getNombre(), funko2.getNombre()),
                () -> assertEquals(foundFunkos.get(1).getFechaLanzamiento(), funko2.getFechaLanzamiento()),
                () -> assertEquals(foundFunkos.get(1).getPrecio(), funko2.getPrecio()),
                () -> assertEquals(foundFunkos.get(1).getId2(), funko2.getId2()),
                () -> assertEquals(foundFunkos.get(1).getCod(), funko2.getCod()));
    }

    @Test
    void findByNombre() throws SQLException {
        Funko funko1 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko funko2 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(96L)
                .nombre("Mate")
                .modelo(Modelo.DISNEY)
                .precio(200.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        funkoRepository.save(funko1).block();
        funkoRepository.save(funko2).block();
        List<Funko> foundFunkos = funkoRepository.findByNombre("Rayo McQueen").collectList().block();
        assertAll(() -> assertNotNull(foundFunkos),
                () -> assertEquals(1, foundFunkos.size()),
                () -> assertEquals(funko1.getModelo(), foundFunkos.get(0).getModelo()),
                () -> assertEquals(funko1.getNombre(), foundFunkos.get(0).getNombre()),
                () -> assertEquals(funko1.getFechaLanzamiento(), foundFunkos.get(0).getFechaLanzamiento()),
                () -> assertEquals(funko1.getPrecio(), foundFunkos.get(0).getPrecio()),
                () -> assertEquals(funko1.getId2(), foundFunkos.get(0).getId2()),
                () -> assertEquals(funko1.getCod(), foundFunkos.get(0).getCod())
        );
    }

    @Test
    void deleteById() throws SQLException {
        Funko funko1 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko savedFunko = funkoRepository.save(funko1).block();
        funkoRepository.deleteById(savedFunko.getId2()).block();
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.getId2()).blockOptional();
        assertFalse(foundFunko.isPresent());
    }

    @Test
    void deleteAll() throws SQLException {
        Funko funko1 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(95L)
                .nombre("Rayo McQueen")
                .modelo(Modelo.DISNEY)
                .precio(100.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        Funko funko2 = Funko.builder()
                .cod(UUID.randomUUID())
                .id2(96L)
                .nombre("Mate")
                .modelo(Modelo.DISNEY)
                .precio(200.0)
                .fechaLanzamiento(LocalDate.parse("2021-10-07"))
                .build();
        funkoRepository.save(funko1).block();
        funkoRepository.save(funko2).block();
        funkoRepository.deleteAll().block();
        List<Funko> foundFunkos = funkoRepository.findAll().collectList().block();
        assertEquals(0, foundFunkos.size());
    }
}