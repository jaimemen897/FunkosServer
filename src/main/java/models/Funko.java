package models;

import adapters.LocalDateAdapter;
import com.google.gson.annotations.JsonAdapter;
import enums.Modelo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonAdapter(LocalDateAdapter.class)
public class Funko {

    private final UUID cod;
    private final Long id2;
    private final LocalDate fechaLanzamiento;
    private String nombre;
    private Modelo modelo;
    private double precio;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
