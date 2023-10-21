package adapters;

import com.google.gson.*;
import enums.Modelo;
import models.Funko;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;
import java.time.LocalDate;

public class FunkoAdapter implements JsonSerializer<Funko>, JsonDeserializer<Funko> {
    @Override
    public JsonElement serialize(Funko funko, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cod", funko.getCod().toString());
        jsonObject.addProperty("id2", funko.getId2());
        jsonObject.addProperty("fechaLanzamiento", funko.getFechaLanzamiento().toString());
        jsonObject.addProperty("nombre", funko.getNombre());
        jsonObject.addProperty("modelo", funko.getModelo().toString());
        jsonObject.addProperty("precio", funko.getPrecio());
        jsonObject.addProperty("createdAt", funko.getCreatedAt().toString());
        jsonObject.addProperty("updatedAt", funko.getUpdatedAt().toString());
        return jsonObject;
    }

    @Override
    public Funko deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        UUID cod = UUID.fromString(jsonObject.get("cod").getAsString());
        Long id2 = jsonObject.get("id2").getAsLong();
        LocalDate fechaLanzamiento = LocalDate.parse(jsonObject.get("fechaLanzamiento").getAsString());
        String nombre = jsonObject.get("nombre").getAsString();
        Modelo modelo = Modelo.valueOf(jsonObject.get("modelo").getAsString());
        double precio = jsonObject.get("precio").getAsDouble();
        LocalDateTime createdAt = LocalDateTime.parse(jsonObject.get("createdAt").getAsString());
        LocalDateTime updatedAt = LocalDateTime.parse(jsonObject.get("updatedAt").getAsString());

        return Funko.builder()
                .cod(cod)
                .id2(id2)
                .fechaLanzamiento(fechaLanzamiento)
                .nombre(nombre)
                .modelo(modelo)
                .precio(precio)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}