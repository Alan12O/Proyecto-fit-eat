package util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.http.HttpServletRequest;
import modelo.ProductoDespensa;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JsonUtil {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeHierarchyAdapter(ProductoDespensa.class, new ProductoDespensaSerializer())
                .setPrettyPrinting()
                .create();
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clase) {
        return gson.fromJson(json, clase);
    }

    public static String leerCuerpoRequest(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(FORMATTER));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String texto = in.nextString();
            if (texto == null || texto.trim().isEmpty() || "null".equalsIgnoreCase(texto.trim())) {
                return null;
            }
            return LocalDate.parse(texto.trim(), FORMATTER);
        }
    }

    public static class ProductoDespensaSerializer implements JsonSerializer<ProductoDespensa> {
        @Override
        public JsonElement serialize(ProductoDespensa src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", src.getId());
            obj.add("ingrediente", context.serialize(src.getIngrediente()));
            obj.addProperty("cantidad", src.getCantidad());

            if (src.getFechaCaducidad() != null) {
                obj.addProperty("fechaCaducidad", src.getFechaCaducidad().toString());
            } else {
                obj.add("fechaCaducidad", JsonNull.INSTANCE);
            }

            obj.addProperty("categoria", (src.getIngrediente() != null) ? src.getIngrediente().getCategoria() : "");
            obj.addProperty("diasParaCaducar", src.diasParaCaducar());
            obj.addProperty("estaPorCaducar", src.estaPorCaducar(3));
            obj.addProperty("nivelUrgencia", src.calcularNivelUrgencia());
            return obj;
        }
    }
}
