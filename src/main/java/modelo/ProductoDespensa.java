package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class ProductoDespensa {
    protected int id;
    protected Ingrediente ingrediente;
    protected double cantidad;
    protected LocalDate fechaCaducidad;

    public ProductoDespensa() {
    }

    public ProductoDespensa(int id, Ingrediente ingrediente, double cantidad, LocalDate fechaCaducidad) {
        this.id = id;
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.fechaCaducidad = fechaCaducidad;
    }

    public ProductoDespensa(Ingrediente ingrediente, double cantidad, LocalDate fechaCaducidad) {
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.fechaCaducidad = fechaCaducidad;
    }

    public void agregarCantidad(double c) {
        if (c > 0) {
            this.cantidad += c;
        }
    }

    public boolean descontarCantidad(double c) {
        if (c > 0 && this.cantidad >= c) {
            this.cantidad -= c;
            return true;
        }
        return false;
    }

    public long diasParaCaducar() {
        if (fechaCaducidad == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaCaducidad);
    }

    public abstract boolean estaPorCaducar(int diasUmbral);

    public abstract int calcularNivelUrgencia();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    @Override
    public String toString() {
        return "ProductoDespensa{" +
                "id=" + id +
                ", ingrediente=" + ingrediente +
                ", cantidad=" + cantidad +
                ", fechaCaducidad=" + fechaCaducidad +
                '}';
    }
}
