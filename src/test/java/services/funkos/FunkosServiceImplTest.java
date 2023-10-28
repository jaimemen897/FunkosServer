package services.funkos;

import enums.Modelo;
import models.Funko;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repositories.funkos.FunkoRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkosServiceImplTest {
    @Mock
    FunkoRepositoryImpl repository;

    @Mock
    FunkoStorageImpl storage;

    @Mock
    FunkosNotifications notifications;

    @InjectMocks
    FunkosServiceImpl service;

    @Test
    void findAll() throws SQLException {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Mate").modelo(Modelo.DISNEY).precio(90.0).fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.findAll().collectList().block();

        assertAll("findAll",
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Rayo McQueen", result.get(0).getNombre()),
                () -> assertEquals("Mate", result.get(1).getNombre()),
                () -> assertEquals(100.0, result.get(0).getPrecio()),
                () -> assertEquals(90.0, result.get(1).getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.get(0).getFechaLanzamiento()),
                () -> assertEquals(LocalDate.parse("2023-10-07"), result.get(1).getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get(0).getModelo()),
                () -> assertEquals(Modelo.DISNEY, result.get(1).getModelo()),
                () -> assertEquals(1L, result.get(0).getId2()),
                () -> assertEquals(2L, result.get(1).getId2()),
                () -> assertNotNull(result.get(0).getCod()),
                () -> assertNotNull(result.get(1).getCod())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void findByNombre() {
        var funkos = List.of(Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build());

        when(repository.findByNombre("Rayo McQueen")).thenReturn(Flux.fromIterable(funkos));
        var result = service.findByNombre("Rayo McQueen").collectList().block();

        assertAll("findByNombre",
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Rayo McQueen", result.get(0).getNombre()),
                () -> assertEquals(100.0, result.get(0).getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.get(0).getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get(0).getModelo()),
                () -> assertEquals(1L, result.get(0).getId2()),
                () -> assertNotNull(result.get(0).getCod())
        );

        verify(repository, times(1)).findByNombre("Rayo McQueen");
    }

    @Test
    void findById() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        var result = service.findById(1L).blockOptional();

        assertAll("findById",
                () -> assertTrue(result.isPresent()),
                () -> assertEquals("Rayo McQueen", result.get().getNombre()),
                () -> assertEquals(100.0, result.get().getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.get().getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get().getModelo()),
                () -> assertEquals(1L, result.get().getId2()),
                () -> assertNotNull(result.get().getCod())
        );

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findByIdNoExiste() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findById(1L)).thenReturn(Mono.empty());

        var result = assertThrows(Exception.class, () -> service.findById(1L).blockOptional());

        assertTrue(result.getMessage().contains("exceptions.funko.FunkoNotFoundException: Funko con ID: 1 no encontrado"));

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findBycodigo() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findByCodigo(funko.getCod().toString())).thenReturn(Mono.just(funko));
        var result = service.findByCodigo(funko.getCod().toString()).blockOptional();

        assertAll("findByCodigo"
                , () -> assertTrue(result.isPresent())
                , () -> assertEquals("Rayo McQueen", result.get().getNombre())
                , () -> assertEquals(100.0, result.get().getPrecio())
                , () -> assertEquals(LocalDate.parse("2021-10-07"), result.get().getFechaLanzamiento())
                , () -> assertEquals(Modelo.DISNEY, result.get().getModelo())
                , () -> assertEquals(1L, result.get().getId2())
                , () -> assertNotNull(result.get().getCod())
        );

        verify(repository, times(1)).findByCodigo(funko.getCod().toString());
    }

    @Test
    void findBycodigoNoExiste() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2024-10-07")).build();

        when(repository.findByCodigo(funko.getCod().toString())).thenReturn(Mono.empty());

        var result = assertThrows(Exception.class, () -> service.findByCodigo(funko.getCod().toString()).blockOptional());

        assertTrue(result.getMessage().contains("exceptions.funko.FunkoNotFoundException: No se ha encontrado ningún funko con el código: " + funko.getCod().toString()));

        verify(repository, times(1)).findByCodigo(funko.getCod().toString());
    }

    @Test
    void findByReleaseDate() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findByReleaseDate(LocalDate.parse("2021-10-07"))).thenReturn(Flux.just(funko));
        var result = service.findByReleaseDate(LocalDate.parse("2021-10-07")).collectList().block();

        assertAll("findByReleaseDate"
                , () -> assertNotNull(result)
                , () -> assertEquals(1, result.size())
                , () -> assertEquals("Rayo McQueen", result.get(0).getNombre())
                , () -> assertEquals(100.0, result.get(0).getPrecio())
                , () -> assertEquals(LocalDate.parse("2021-10-07"), result.get(0).getFechaLanzamiento())
                , () -> assertEquals(Modelo.DISNEY, result.get(0).getModelo())
                , () -> assertEquals(1L, result.get(0).getId2())
                , () -> assertNotNull(result.get(0).getCod())
        );

        verify(repository, times(1)).findByReleaseDate(LocalDate.parse("2021-10-07"));
    }

    @Test
    void findByReleaseDateNoExiste() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2024-10-07")).build();

        when(repository.findByReleaseDate(LocalDate.parse("2024-10-07"))).thenReturn(Flux.empty());

        var result = assertThrows(Exception.class, () -> service.findByReleaseDate(LocalDate.parse("2024-10-07")).collectList().block());

        assertTrue(result.getMessage().contains("exceptions.funko.FunkoNotFoundException: No se ha encontrado ningún funko con la fecha de lanzamiento: 2024-10-07"));

        verify(repository, times(1)).findByReleaseDate(LocalDate.parse("2024-10-07"));
    }

    @Test
    void findByModelo() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findByModelo(Modelo.DISNEY)).thenReturn(Flux.just(funko));
        var result = service.findByModelo(Modelo.DISNEY).collectList().block();

        assertAll("findByModelo"
                , () -> assertNotNull(result)
                , () -> assertEquals(1, result.size())
                , () -> assertEquals("Rayo McQueen", result.get(0).getNombre())
                , () -> assertEquals(100.0, result.get(0).getPrecio())
                , () -> assertEquals(LocalDate.parse("2021-10-07"), result.get(0).getFechaLanzamiento())
                , () -> assertEquals(Modelo.DISNEY, result.get(0).getModelo())
                , () -> assertEquals(1L, result.get(0).getId2())
                , () -> assertNotNull(result.get(0).getCod())
        );

        verify(repository, times(1)).findByModelo(Modelo.DISNEY);
    }

    @Test
    void findByModeloNoExiste() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2024-10-07")).build();

        when(repository.findByModelo(Modelo.DISNEY)).thenReturn(Flux.empty());

        var result = assertThrows(Exception.class, () -> service.findByModelo(Modelo.DISNEY).collectList().block());

        assertTrue(result.getMessage().contains("exceptions.funko.FunkoNotFoundException: No se ha encontrado ningún funko con el modelo: DISNEY"));

        verify(repository, times(1)).findByModelo(Modelo.DISNEY);
    }


    @Test
    void save() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.save(funko)).thenReturn(Mono.just(funko));

        var result = service.saveWithNoNotifications(funko).block();

        assertAll("save",
                () -> assertNotNull(result),
                () -> assertEquals("Rayo McQueen", result.getNombre()),
                () -> assertEquals(100.0, result.getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.getModelo()),
                () -> assertEquals(1L, result.getId2()),
                () -> assertNotNull(result.getCod())
        );

        verify(repository, times(1)).save(funko);
    }

    @Test
    void update() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.update(funko)).thenReturn(Mono.just(funko));

        var result = service.updateWithNoNotifications(funko).block();

        assertAll("update",
                () -> assertEquals("Rayo McQueen", result.getNombre()),
                () -> assertEquals(100.0, result.getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.getModelo()),
                () -> assertEquals(1L, result.getId2()),
                () -> assertNotNull(result.getCod())
        );

        verify(repository, times(1)).update(funko);
    }

    @Test
    void updateNoExiste() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY)
                .precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.findById(1L)).thenReturn(Mono.empty());

        var result = assertThrows(Exception.class, () -> service.updateWithNoNotifications(funko).block());

        assertTrue(result.getMessage().contains("exceptions.funko.FunkoNotFoundException: Funko con id 1 no encontrado"));

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deleteById() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();
        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.deleteById(1L)).thenReturn(Mono.just(true));

        var result = service.deleteByIdWithoutNotification(1L).block();

        assertEquals(result, funko);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdNoExiste() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();
        when(repository.findById(1L)).thenReturn(Mono.empty());

        var result = assertThrows(Exception.class, () -> service.deleteByIdWithoutNotification(1L).block());
        System.out.println(result.getMessage());
        assertTrue(result.getMessage().contains("exceptions.funko.FunkoNotFoundException: Funko con id 1 no encontrado"));

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deleteAll() {
        var funko = Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

        when(repository.deleteAll()).thenReturn(Mono.empty());

        service.deleteAll().block();

        verify(repository, times(1)).deleteAll();
    }

    @Test
    void testExpensiveFunko() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Mate").modelo(Modelo.DISNEY).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.expensiveFunko().blockOptional();

        assertAll("expensiveFunko",
                () -> assertTrue(result.isPresent()),
                () -> assertEquals("Rayo McQueen", result.get().getNombre()),
                () -> assertEquals(100.0, result.get().getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.get().getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get().getModelo()),
                () -> assertEquals(1L, result.get().getId2()),
                () -> assertNotNull(result.get().getCod())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void testAveragePrice() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Mate").modelo(Modelo.DISNEY).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.averagePrice().block();

        assertEquals(95.0, result);

        verify(repository, times(1)).findAll();
    }

    @Test
    void testGroupByModelo() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Mate").modelo(Modelo.ANIME).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.groupByModelo().block();

        assertAll("groupByModelo",
                () -> assertNotNull(result),
                () -> assertEquals(1, result.get(Modelo.DISNEY).size()),
                () -> assertEquals("Rayo McQueen", result.get(Modelo.DISNEY).get(0).getNombre()),
                () -> assertEquals(100.0, result.get(Modelo.DISNEY).get(0).getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.get(Modelo.DISNEY).get(0).getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get(Modelo.DISNEY).get(0).getModelo()),
                () -> assertEquals(1L, result.get(Modelo.DISNEY).get(0).getId2()),
                () -> assertNotNull(result.get(Modelo.DISNEY).get(0).getCod())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void testFunkosByModelo() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Mate").modelo(Modelo.ANIME).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.funkosByModelo().block();

        assertAll("funkosByModelo",
                () -> assertNotNull(result),
                () -> assertEquals(1, result.get(Modelo.DISNEY)),
                () -> assertEquals(1L, result.get(Modelo.DISNEY))
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void testFunkosIn2023() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Mate").modelo(Modelo.DISNEY).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.funkosIn2023().collectList().block();

        assertAll("funkosIn2023",
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Mate", result.get(0).getNombre()),
                () -> assertEquals(90.0, result.get(0).getPrecio()),
                () -> assertEquals(LocalDate.parse("2023-10-07"), result.get(0).getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get(0).getModelo()),
                () -> assertEquals(2L, result.get(0).getId2()),
                () -> assertNotNull(result.get(0).getCod())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void testNumberStitch() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Stitch").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Stitch").modelo(Modelo.DISNEY).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.numberStitch().block();

        assertEquals(2.0, result);

        verify(repository, times(1)).findAll();
    }

    @Test
    void testFunkoStitch() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Stitch").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Stitch").modelo(Modelo.DISNEY).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );

        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        var result = service.funkoStitch().collectList().block();

        assertAll("funkoStitch",
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Stitch", result.get(0).getNombre()),
                () -> assertEquals(100.0, result.get(0).getPrecio()),
                () -> assertEquals(LocalDate.parse("2021-10-07"), result.get(0).getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get(0).getModelo()),
                () -> assertEquals(1L, result.get(0).getId2()),
                () -> assertNotNull(result.get(0).getCod()),
                () -> assertEquals("Stitch", result.get(1).getNombre()),
                () -> assertEquals(90.0, result.get(1).getPrecio()),
                () -> assertEquals(LocalDate.parse("2023-10-07"), result.get(1).getFechaLanzamiento()),
                () -> assertEquals(Modelo.DISNEY, result.get(1).getModelo()),
                () -> assertEquals(2L, result.get(1).getId2()),
                () -> assertNotNull(result.get(1).getCod())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void testExportToJson() {
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).id2(1L).nombre("Stitch").modelo(Modelo.DISNEY).precio(100.0)
                        .fechaLanzamiento(LocalDate.parse("2021-10-07")).build(),
                Funko.builder().cod(UUID.randomUUID()).id2(2L).nombre("Stitch").modelo(Modelo.DISNEY).precio(90.0)
                        .fechaLanzamiento(LocalDate.parse("2023-10-07")).build()
        );
        when(repository.deleteAll()).thenReturn(Mono.empty());
        when(repository.exportJson("src/main/resources/funkos.json")).thenReturn(Mono.empty());

        service.exportToJson("src/main/resources/funkos.json");

        verify(repository, times(1)).deleteAll();
        verify(repository, times(1)).exportJson("src/main/resources/funkos.json");
    }
}