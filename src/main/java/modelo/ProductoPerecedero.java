package modelo;

import java.time.LocalDate;

public class ProductoPerecedero extends ProductoDespensa {

    public ProductoPerecedero() {
        super();
    }

    public ProductoPerecedero(int id, Ingrediente ingrediente, double cantidad, LocalDate fechaCaducidad) {
        super(id, ingrediente, cantidad, fechaCaducidad);
    }

    public ProductoPerecedero(Ingrediente ingrediente, double cantidad, LocalDate fechaCaducidad) {
        super(ingrediente, cantidad, fechaCaducidad);
    }

    @Override
    public boolean estaPorCaducar(int diasUmbral) {
        if (fechaCaducidad == null) {
            return false;
        }
        return diasParaCaducar() <= diasUmbral;
    }

    @Override
    public int calcularNivelUrgencia() {
        if (fechaCaducidad == null) {
            return 0;
        }
        long dias = diasParaCaducar();
        if (dias < 0) {
            return 4;
        } else if (dias <= 1) {
            return 3;
        } else if (dias <= 3) {
            return 2;
        } else if (dias <= 7) {
            return 1;
        } else {
            return 0;
        }
    }
}
