package controlador;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.GestorDespensaService;
import util.JsonUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "DespensaServlet", urlPatterns = {"/api/despensa"})
public class DespensaServlet extends HttpServlet {
    private GestorDespensaService service;

    @Override
    public void init() throws ServletException {
        super.init();
        this.service = new GestorDespensaService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("despensa", service.listarDespensa());
        respuesta.put("alertas", service.obtenerAlertasCaducidad());

        resp.getWriter().write(JsonUtil.toJson(respuesta));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);
        try {
            String cuerpo = JsonUtil.leerCuerpoRequest(req);
            int idIngrediente = 0;
            double cantidad = 0.0;
            LocalDate fechaCaducidad = null;

            if (cuerpo != null && !cuerpo.trim().isEmpty()) {
                JsonObject json = JsonParser.parseString(cuerpo).getAsJsonObject();
                if (json.has("idIngrediente")) {
                    idIngrediente = json.get("idIngrediente").getAsInt();
                }
                if (json.has("cantidad")) {
                    cantidad = json.get("cantidad").getAsDouble();
                }
                if (json.has("fechaCaducidad") && !json.get("fechaCaducidad").isJsonNull()) {
                    String strFecha = json.get("fechaCaducidad").getAsString();
                    if (strFecha != null && !strFecha.trim().isEmpty()) {
                        fechaCaducidad = LocalDate.parse(strFecha.trim());
                    }
                }
            } else {
                String paramId = req.getParameter("idIngrediente");
                String paramCant = req.getParameter("cantidad");
                String paramFecha = req.getParameter("fechaCaducidad");

                if (paramId != null) idIngrediente = Integer.parseInt(paramId);
                if (paramCant != null) cantidad = Double.parseDouble(paramCant);
                if (paramFecha != null && !paramFecha.trim().isEmpty()) {
                    fechaCaducidad = LocalDate.parse(paramFecha.trim());
                }
            }

            if (idIngrediente <= 0 || cantidad <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(crearJsonRespuesta(false, "El ingrediente y la cantidad deben ser mayores a 0."));
                return;
            }

            service.agregarADespensa(idIngrediente, cantidad, fechaCaducidad);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(crearJsonRespuesta(true, "Lote agregado exitosamente a la despensa."));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(crearJsonRespuesta(false, "Error al agregar producto: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);
        try {
            String cuerpo = JsonUtil.leerCuerpoRequest(req);
            int id = 0;
            double cantidad = 0.0;

            if (cuerpo != null && !cuerpo.trim().isEmpty()) {
                JsonObject json = JsonParser.parseString(cuerpo).getAsJsonObject();
                if (json.has("id")) {
                    id = json.get("id").getAsInt();
                }
                if (json.has("cantidad")) {
                    cantidad = json.get("cantidad").getAsDouble();
                }
            } else {
                String paramId = req.getParameter("id");
                String paramCant = req.getParameter("cantidad");
                if (paramId != null) id = Integer.parseInt(paramId);
                if (paramCant != null) cantidad = Double.parseDouble(paramCant);
            }

            if (id <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(crearJsonRespuesta(false, "ID de lote inválido."));
                return;
            }

            service.actualizarCantidad(id, cantidad);
            resp.getWriter().write(crearJsonRespuesta(true, "Cantidad de lote actualizada exitosamente."));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(crearJsonRespuesta(false, "Error al actualizar cantidad: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);
        try {
            String paramId = req.getParameter("id");
            int id = 0;

            if (paramId != null && !paramId.trim().isEmpty()) {
                id = Integer.parseInt(paramId.trim());
            } else {
                String cuerpo = JsonUtil.leerCuerpoRequest(req);
                if (cuerpo != null && !cuerpo.trim().isEmpty()) {
                    JsonObject json = JsonParser.parseString(cuerpo).getAsJsonObject();
                    if (json.has("id")) {
                        id = json.get("id").getAsInt();
                    }
                }
            }

            if (id <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(crearJsonRespuesta(false, "Debe especificar un ID válido para eliminar."));
                return;
            }

            service.eliminarDeDespensa(id);
            resp.getWriter().write(crearJsonRespuesta(true, "Lote eliminado de la despensa exitosamente."));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(crearJsonRespuesta(false, "Error al eliminar de despensa: " + e.getMessage()));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void configurarHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private String crearJsonRespuesta(boolean exito, String mensaje) {
        JsonObject obj = new JsonObject();
        obj.addProperty("exito", exito);
        obj.addProperty("mensaje", mensaje);
        return JsonUtil.toJson(obj);
    }
}
