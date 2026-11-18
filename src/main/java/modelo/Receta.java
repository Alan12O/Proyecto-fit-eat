package modelo;

import java.util.ArrayList;
import java.util.List;

public class Receta {
    private int id;
    private String nombre;
    private String descripcion;
    private String instrucciones;
    private int tiempoPreparacion;
    private List<RecetaIngrediente> ingredientes = new ArrayList<>();

    private boolean tieneIngredientesSuficientes;
    private List<String> ingredientesFaltantes = new ArrayList<>();
    private List<String> ingredientesRescatados = new ArrayList<>();

    public Receta() {
    }

    public Receta(int id, String nombre, String descripcion, String instrucciones, int tiempoPreparacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.instrucciones = instrucciones;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public boolean tieneIngredientesSuficientes(List<ProductoDespensa> despensa) {
        return tieneIngredientesSuficientes(despensa, 1);
    }

    public boolean tieneIngredientesSuficientes(List<ProductoDespensa> despensa, int porciones) {
        if (porciones <= 0) porciones = 1;
        if (ingredientes == null || ingredientes.isEmpty()) {
            return true;
        }
        for (RecetaIngrediente ri : ingredientes) {
            if (ri == null || ri.getIngrediente() == null) {
                continue;
            }
            int idIng = ri.getIngrediente().getId();
            double disponible = 0.0;
            if (despensa != null) {
                for (ProductoDespensa p : despensa) {
                    if (p != null && p.getIngrediente() != null && p.getIngrediente().getId() == idIng) {
                        disponible += p.getCantidad();
                    }
                }
            }
            if (disponible < (ri.getCantidadNecesaria() * porciones)) {
                return false;
            }
        }
        return true;
    }

    public List<String> ingredientesFaltantes(List<ProductoDespensa> despensa) {
        return ingredientesFaltantes(despensa, 1);
    }

    public List<String> ingredientesFaltantes(List<ProductoDespensa> despensa, int porciones) {
        if (porciones <= 0) porciones = 1;
        List<String> faltantes = new ArrayList<>();
        if (ingredientes == null || ingredientes.isEmpty()) {
            return faltantes;
        }
        for (RecetaIngrediente ri : ingredientes) {
            if (ri == null || ri.getIngrediente() == null) {
                continue;
            }
            int idIng = ri.getIngrediente().getId();
            double disponible = 0.0;
            if (despensa != null) {
                for (ProductoDespensa p : despensa) {
                    if (p != null && p.getIngrediente() != null && p.getIngrediente().getId() == idIng) {
                        disponible += p.getCantidad();
                    }
                }
            }
            double requerida = ri.getCantidadNecesaria() * porciones;
            if (disponible < requerida) {
                double falta = requerida - disponible;
                String nom = ri.getIngrediente().getNombre();
                String unidad = ri.getIngrediente().getUnidadMedida();
                faltantes.add(String.format("%s: faltan %.1f %s (se requieren %.1f %s, en despensa: %.1f %s)",
                        nom, falta, unidad, requerida, unidad, disponible, unidad));
            }
        }
        return faltantes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public List<RecetaIngrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<RecetaIngrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public boolean isTieneIngredientesSuficientes() {
        return tieneIngredientesSuficientes;
    }

    public void setTieneIngredientesSuficientes(boolean tieneIngredientesSuficientes) {
        this.tieneIngredientesSuficientes = tieneIngredientesSuficientes;
    }

    public List<String> getIngredientesFaltantes() {
        return ingredientesFaltantes;
    }

    public void setIngredientesFaltantes(List<String> ingredientesFaltantes) {
        this.ingredientesFaltantes = ingredientesFaltantes;
    }

    public List<String> getIngredientesRescatados() {
        return ingredientesRescatados;
    }

    public void setIngredientesRescatados(List<String> ingredientesRescatados) {
        this.ingredientesRescatados = ingredientesRescatados;
    }

    @Override
    public String toString() {
        return "Receta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tiempoPreparacion=" + tiempoPreparacion +
                ", ingredientes=" + ingredientes.size() +
                '}';
    }
}
