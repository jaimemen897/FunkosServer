package adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Funko;

import java.lang.reflect.Type;

public class LocalDateAdapter implements JsonSerializer<Funko> {

    @Override
    public JsonElement serialize(Funko funko, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cod", funko.getCod().toString());
        jsonObject.addProperty("id2", funko.getId2());
        jsonObject.addProperty("nombre", funko.getNombre());
        jsonObject.addProperty("modelo", funko.getModelo().toString());
        jsonObject.addProperty("precio", funko.getPrecio());
        jsonObject.addProperty("fechaLanzamiento", funko.getFechaLanzamiento().toString());
        return jsonObject;
    }
}