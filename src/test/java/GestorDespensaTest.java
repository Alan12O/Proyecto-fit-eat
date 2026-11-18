import dao.ConexionBD;
import dao.DespensaDAO;
import dao.IngredienteDAO;
import dao.RecetaDAO;
import estrategia.EstrategiaAntiDesperdicio;
import estrategia.EstrategiaDisponibilidadTotal;
import modelo.Ingrediente;
import modelo.ProductoDespensa;
import modelo.Receta;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import service.GestorDespensaService;
import util.JsonUtil;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GestorDespensaTest {

    @BeforeAll
    public static void checkDatabase() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        assertNotNull(conn, "La conexión a la base de datos MySQL debe ser exitosa.");
    }

    @Test
    @Order(1)
    public void testIngredientes() {
        IngredienteDAO dao = new IngredienteDAO();
        List<Ingrediente> lista = dao.listarTodos();
        assertNotNull(lista);
        assertTrue(lista.size() >= 8, "Debe haber al menos 8 ingredientes");

        Ingrediente primero = dao.buscarPorId(lista.get(0).getId());
        assertNotNull(primero);
        assertTrue(primero.validarDatos());
    }

    @Test
    @Order(2)
    public void testDespensa() {
        DespensaDAO dao = new DespensaDAO();
        List<ProductoDespensa> lista = dao.listarTodoConDetalle();
        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "La despensa no debe estar vacía");

        for (ProductoDespensa p : lista) {
            assertNotNull(p.getIngrediente());
            assertTrue(p.getCantidad() > 0);
        }
    }

    @Test
    @Order(3)
    public void testRecetasYJson() {
        RecetaDAO dao = new RecetaDAO();
        List<Receta> recetas = dao.listarTodas();
        assertTrue(recetas.size() >= 8, "Deben existir al menos 8 recetas cargadas");

        for (Receta r : recetas) {
            assertFalse(r.getIngredientes().isEmpty(), "La receta " + r.getNombre() + " debe tener ingredientes cargados desde recetas_ingredientes.json");
        }
    }

    @Test
    @Order(4)
    public void testEstrategiasYAlertas() {
        GestorDespensaService service = new GestorDespensaService();

        List<String> alertas = service.obtenerAlertasCaducidad();
        assertNotNull(alertas);
        assertFalse(alertas.isEmpty(), "Debe haber alertas de caducidad con los datos iniciales");

        service.setEstrategia(new EstrategiaAntiDesperdicio());
        List<Receta> antiDesp = service.obtenerRecetasRecomendadas();
        assertFalse(antiDesp.isEmpty());
        System.out.println("=== ANTIDESPERDICIO ===");
        for (Receta r : antiDesp) {
            System.out.println("  " + r.getId() + ": " + r.getNombre() + " (suf: " + r.isTieneIngredientesSuficientes() + ", faltan: " + r.getIngredientesFaltantes().size() + ")");
        }

        service.setEstrategia(new EstrategiaDisponibilidadTotal());
        List<Receta> dispTotal = service.obtenerRecetasRecomendadas();
        assertFalse(dispTotal.isEmpty());
        System.out.println("=== DISPONIBILIDAD TOTAL ===");
        for (Receta r : dispTotal) {
            System.out.println("  " + r.getId() + ": " + r.getNombre() + " (suf: " + r.isTieneIngredientesSuficientes() + ", faltan: " + r.getIngredientesFaltantes().size() + ")");
        }

        String json = JsonUtil.toJson(dispTotal);
        assertNotNull(json);
        assertTrue(json.contains("tieneIngredientesSuficientes"));
        assertTrue(json.contains("ingredientesFaltantes"));
    }

    @Test
    @Order(5)
    public void testCrudDespensa() {
        GestorDespensaService service = new GestorDespensaService();

        service.agregarADespensa(3, 350.0, LocalDate.now().plusDays(5));
        List<ProductoDespensa> despensa = service.listarDespensa();
        ProductoDespensa loteCreado = despensa.stream()
                .filter(p -> p.getIngrediente().getId() == 3 && p.getCantidad() == 350.0)
                .findFirst()
                .orElse(null);
        assertNotNull(loteCreado, "El lote agregado debe existir en la despensa");
        assertNull(loteCreado.getFechaCaducidad(), "Los ingredientes no perecederos deben guardar fecha NULL");

        service.actualizarCantidad(loteCreado.getId(), 400.0);
        despensa = service.listarDespensa();
        ProductoDespensa loteActualizado = despensa.stream()
                .filter(p -> p.getId() == loteCreado.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(loteActualizado);
        assertEquals(400.0, loteActualizado.getCantidad(), 0.001);

        service.eliminarDeDespensa(loteCreado.getId());
        despensa = service.listarDespensa();
        boolean existe = despensa.stream().anyMatch(p -> p.getId() == loteCreado.getId());
        assertFalse(existe, "El lote debe haber sido eliminado");
    }

    @Test
    @Order(6)
    public void testCocinarRecetaYDescuentoFIFO() {
        GestorDespensaService service = new GestorDespensaService();
        DespensaDAO dao = new DespensaDAO();

        List<ProductoDespensa> despensaAntes = service.listarDespensa();
        double lecheAntes = despensaAntes.stream()
                .filter(p -> p.getIngrediente().getId() == 1)
                .mapToDouble(ProductoDespensa::getCantidad)
                .sum();

        try {

            service.cocinarReceta(1);

            List<ProductoDespensa> despensaDespues = service.listarDespensa();
            double lecheDespues = despensaDespues.stream()
                    .filter(p -> p.getIngrediente().getId() == 1)
                    .mapToDouble(ProductoDespensa::getCantidad)
                    .sum();

            assertEquals(lecheAntes - 250.0, lecheDespues, 0.001, "La cantidad de leche total debe haberse descontado exactamente en 250 ml por FIFO");
        } finally {

            for (ProductoDespensa p : despensaAntes) {
                dao.actualizarCantidad(p.getId(), p.getCantidad());
            }
        }
    }
}
