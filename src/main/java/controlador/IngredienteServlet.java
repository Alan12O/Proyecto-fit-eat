package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.GestorDespensaService;
import util.JsonUtil;

import java.io.IOException;

@WebServlet(name = "IngredienteServlet", urlPatterns = {"/api/ingredientes"})
public class IngredienteServlet extends HttpServlet {
    private GestorDespensaService service;

    @Override
    public void init() throws ServletException {
        super.init();
        this.service = new GestorDespensaService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().write(JsonUtil.toJson(service.listarIngredientes()));
    }
}
