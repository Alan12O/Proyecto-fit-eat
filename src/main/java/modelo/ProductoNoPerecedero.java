package modelo;

import java.time.LocalDate;

public class ProductoNoPerecedero extends ProductoDespensa {

    public ProductoNoPerecedero() {
        super();
        this.fechaCaducidad = null;
    }

    public ProductoNoPerecedero(int id, Ingrediente ingrediente, double cantidad, LocalDate fechaCaducidad) {
        super(id, ingrediente, cantidad, null);
    }

    public ProductoNoPerecedero(int id, Ingrediente ingrediente, double cantidad) {
        super(id, ingrediente, cantidad, null);
    }

    public ProductoNoPerecedero(Ingrediente ingrediente, double cantidad) {
        super(ingrediente, cantidad, null);
    }

    @Override
    public boolean estaPorCaducar(int diasUmbral) {
        return false;
    }

    @Override
    public int calcularNivelUrgencia() {
        return 0;
    }
}
