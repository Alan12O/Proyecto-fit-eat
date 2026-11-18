package service;

import dao.DespensaDAO;
import dao.IngredienteDAO;
import dao.RecetaDAO;
import estrategia.EstrategiaAntiDesperdicio;
import estrategia.IEstrategiaSugerencia;
import modelo.Ingrediente;
import modelo.ProductoDespensa;
import modelo.Receta;
import modelo.RecetaIngrediente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorDespensaService {
    private DespensaDAO despensaDAO;
    private IngredienteDAO ingredienteDAO;
    private RecetaDAO recetaDAO;
    private IEstrategiaSugerencia estrategia;

    public GestorDespensaService() {
        this.despensaDAO = new DespensaDAO();
        this.ingredienteDAO = new IngredienteDAO();
        this.recetaDAO = new RecetaDAO();
        this.estrategia = new EstrategiaAntiDesperdicio();
    }

    public GestorDespensaService(DespensaDAO despensaDAO, IngredienteDAO ingredienteDAO, RecetaDAO recetaDAO, IEstrategiaSugerencia estrategia) {
        this.despensaDAO = despensaDAO;
        this.ingredienteDAO = ingredienteDAO;
        this.recetaDAO = recetaDAO;
        this.estrategia = (estrategia != null) ? estrategia : new EstrategiaAntiDesperdicio();
    }

    public void setEstrategia(IEstrategiaSugerencia est) {
        if (est != null) {
            this.estrategia = est;
        }
    }

    public List<Ingrediente> listarIngredientes() {
        return ingredienteDAO.listarTodos();
    }

    public List<ProductoDespensa> listarDespensa() {
        return despensaDAO.listarTodoConDetalle();
    }

    public void agregarADespensa(int idIngrediente, double cantidad, LocalDate fechaCaducidad) {
        Ingrediente ing = ingredienteDAO.buscarPorId(idIngrediente);
        if (ing != null && "NO_PERECEDERO".equalsIgnoreCase(ing.getCategoria())) {
            fechaCaducidad = null;
        }
        despensaDAO.insertar(idIngrediente, cantidad, fechaCaducidad);
    }

    public void actualizarCantidad(int id, double cantidad) {
        despensaDAO.actualizarCantidad(id, cantidad);
    }

    public void eliminarDeDespensa(int id) {
        despensaDAO.eliminar(id);
    }

    public List<String> obtenerAlertasCaducidad() {
        List<ProductoDespensa> despensa = despensaDAO.listarTodoConDetalle();
        List<String> alertas = new ArrayList<>();

        for (ProductoDespensa p : despensa) {
            if (p.estaPorCaducar(3)) {
                long dias = p.diasParaCaducar();
                String nombre = (p.getIngrediente() != null) ? p.getIngrediente().getNombre() : "Producto";
                String unidad = (p.getIngrediente() != null) ? p.getIngrediente().getUnidadMedida() : "";
                String fecha = (p.getFechaCaducidad() != null) ? p.getFechaCaducidad().toString() : "N/A";

                if (dias < 0) {
                    alertas.add(String.format("¡VENCIDO! %s (%.1f %s) caducó hace %d día(s) (Fecha: %s). Se recomienda desecharlo.",
                            nombre, p.getCantidad(), unidad, Math.abs(dias), fecha));
                } else if (dias == 0) {
                    alertas.add(String.format("¡VENCE HOY! %s (%.1f %s) vence HOY (%s). ¡Consumelo pronto!",
                            nombre, p.getCantidad(), unidad, fecha));
                } else {
                    alertas.add(String.format("¡POR VENCER! %s (%.1f %s) vencerá en %d día(s) (Fecha: %s).",
                            nombre, p.getCantidad(), unidad, dias, fecha));
                }
            }
        }
        return alertas;
    }

    public List<Receta> obtenerRecetasRecomendadas() {
        List<Receta> recetas = recetaDAO.listarTodas();
        List<ProductoDespensa> despensa = despensaDAO.listarTodoConDetalle();

        if (estrategia == null) {
            estrategia = new EstrategiaAntiDesperdicio();
        }

        List<Receta> recetasOrdenadas = estrategia.ordenarYFiltrar(recetas, despensa);

        for (Receta r : recetasOrdenadas) {
            r.setTieneIngredientesSuficientes(r.tieneIngredientesSuficientes(despensa));
            r.setIngredientesFaltantes(r.ingredientesFaltantes(despensa));

            List<String> rescatados = new ArrayList<>();
            if (r.getIngredientes() != null && despensa != null) {
                for (RecetaIngrediente ri : r.getIngredientes()) {
                    if (ri == null || ri.getIngrediente() == null) continue;
                    int idIng = ri.getIngrediente().getId();
                    for (ProductoDespensa p : despensa) {
                        if (p != null && p.getIngrediente() != null && p.getIngrediente().getId() == idIng) {
                            if (p.estaPorCaducar(3) && !rescatados.contains(ri.getIngrediente().getNombre())) {
                                rescatados.add(ri.getIngrediente().getNombre());
                            }
                        }
                    }
                }
            }
            r.setIngredientesRescatados(rescatados);
        }

        return recetasOrdenadas;
    }

    public void cocinarReceta(int idReceta) {
        cocinarReceta(idReceta, 1);
    }

    public void cocinarReceta(int idReceta, int porciones) {
        if (porciones <= 0) porciones = 1;

        Receta receta = recetaDAO.buscarPorId(idReceta);
        if (receta == null) {
            throw new IllegalArgumentException("No se encontró la receta con ID: " + idReceta);
        }

        List<ProductoDespensa> despensa = despensaDAO.listarTodoConDetalle();
        if (!receta.tieneIngredientesSuficientes(despensa, porciones)) {
            List<String> faltantes = receta.ingredientesFaltantes(despensa, porciones);
            throw new IllegalStateException("Ingredientes insuficientes para " + porciones + " porción(es) de '" + receta.getNombre() + "': " + String.join(", ", faltantes));
        }

        for (RecetaIngrediente ri : receta.getIngredientes()) {
            if (ri != null && ri.getIngrediente() != null) {
                despensaDAO.descontarFIFO(ri.getIngrediente().getId(), ri.getCantidadNecesaria() * porciones);
            }
        }
    }
}
