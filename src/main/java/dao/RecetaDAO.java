package dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import modelo.Ingrediente;
import modelo.Receta;
import modelo.RecetaIngrediente;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecetaDAO {

    private final IngredienteDAO ingredienteDAO = new IngredienteDAO();

    public List<Receta> listarTodas() {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, instrucciones, tiempo_preparacion FROM recetas ORDER BY id ASC";

        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return lista;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Receta receta = new Receta(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getString("instrucciones"),
                            rs.getInt("tiempo_preparacion")
                    );
                    lista.add(receta);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en RecetaDAO.listarTodas: " + e.getMessage());
        }

        cargarIngredientesDesdeJson(lista);

        return lista;
    }

    public Receta buscarPorId(int id) {
        List<Receta> todas = listarTodas();
        for (Receta r : todas) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    private void cargarIngredientesDesdeJson(List<Receta> recetas) {
        if (recetas == null || recetas.isEmpty()) return;

        Map<Integer, List<RecetaIngrediente>> mapaIngredientes = new HashMap<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("recetas_ingredientes.json")) {
            if (is == null) {
                System.err.println("No se encontró el recurso recetas_ingredientes.json en el classpath");
                return;
            }

            JsonElement raiz = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            if (raiz.isJsonArray()) {
                JsonArray arrayRecetas = raiz.getAsJsonArray();
                for (JsonElement item : arrayRecetas) {
                    if (item.isJsonObject()) {
                        JsonObject objReceta = item.getAsJsonObject();
                        int idReceta = objReceta.get("idReceta").getAsInt();
                        List<RecetaIngrediente> listaIngredientes = new ArrayList<>();

                        if (objReceta.has("ingredientes") && objReceta.get("ingredientes").isJsonArray()) {
                            JsonArray arrIng = objReceta.getAsJsonArray("ingredientes");
                            for (JsonElement ingElem : arrIng) {
                                if (ingElem.isJsonObject()) {
                                    JsonObject ingObj = ingElem.getAsJsonObject();
                                    int idIngrediente = ingObj.get("idIngrediente").getAsInt();
                                    double cantidad = ingObj.get("cantidadNecesaria").getAsDouble();

                                    Ingrediente ing = ingredienteDAO.buscarPorId(idIngrediente);
                                    if (ing != null) {
                                        listaIngredientes.add(new RecetaIngrediente(ing, cantidad));
                                    }
                                }
                            }
                        }
                        mapaIngredientes.put(idReceta, listaIngredientes);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo recetas_ingredientes.json: " + e.getMessage());
        }

        for (Receta r : recetas) {
            if (mapaIngredientes.containsKey(r.getId())) {
                r.setIngredientes(mapaIngredientes.get(r.getId()));
            }
        }
    }
}
