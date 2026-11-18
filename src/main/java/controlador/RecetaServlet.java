package controlador;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import estrategia.EstrategiaAntiDesperdicio;
import estrategia.EstrategiaDisponibilidadTotal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Receta;
import service.GestorDespensaService;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "RecetaServlet", urlPatterns = {"/api/recetas"})
public class RecetaServlet extends HttpServlet {
    private GestorDespensaService service;

    @Override
    public void init() throws ServletException {
        super.init();
        this.service = new GestorDespensaService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);

        String estrategiaParam = req.getParameter("estrategia");
        if ("disponibilidad".equalsIgnoreCase(estrategiaParam)) {
            service.setEstrategia(new EstrategiaDisponibilidadTotal());
        } else {

            service.setEstrategia(new EstrategiaAntiDesperdicio());
        }

        List<Receta> recetas = service.obtenerRecetasRecomendadas();
        resp.getWriter().write(JsonUtil.toJson(recetas));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarHeaders(resp);
        try {
            int idReceta = 0;
            int porciones = 1;
            String cuerpo = JsonUtil.leerCuerpoRequest(req);

            if (cuerpo != null && !cuerpo.trim().isEmpty()) {
                JsonObject json = JsonParser.parseString(cuerpo).getAsJsonObject();
                if (json.has("idReceta")) {
                    idReceta = json.get("idReceta").getAsInt();
                }
                if (json.has("porciones")) {
                    porciones = json.get("porciones").getAsInt();
                }
            }

            if (idReceta <= 0) {
                String param = req.getParameter("idReceta");
                if (param != null && !param.trim().isEmpty()) {
                    idReceta = Integer.parseInt(param.trim());
                }
                String paramPorc = req.getParameter("porciones");
                if (paramPorc != null && !paramPorc.trim().isEmpty()) {
                    porciones = Integer.parseInt(paramPorc.trim());
                }
            }

            if (porciones <= 0) {
                porciones = 1;
            }

            if (idReceta <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(crearJsonRespuesta(false, "Debe especificar un idReceta válido."));
                return;
            }

            service.cocinarReceta(idReceta, porciones);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(crearJsonRespuesta(true, "Se descontaron los lotes de despensa."));

        } catch (IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(crearJsonRespuesta(false, e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(crearJsonRespuesta(false, "Error al cocinar receta: " + e.getMessage()));
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
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private String crearJsonRespuesta(boolean exito, String mensaje) {
        JsonObject obj = new JsonObject();
        obj.addProperty("exito", exito);
        obj.addProperty("mensaje", mensaje);
        return JsonUtil.toJson(obj);
    }
}
