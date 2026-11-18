package estrategia;

import modelo.ProductoDespensa;
import modelo.Receta;
import modelo.RecetaIngrediente;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EstrategiaAntiDesperdicio implements IEstrategiaSugerencia {

    @Override
    public List<Receta> ordenarYFiltrar(List<Receta> recetas, List<ProductoDespensa> despensa) {
        if (recetas == null) {
            return new ArrayList<>();
        }

        List<Receta> copia = new ArrayList<>(recetas);

        copia.sort((r1, r2) -> {
            int score1 = calcularPuntajeUrgencia(r1, despensa);
            int score2 = calcularPuntajeUrgencia(r2, despensa);

            if (score1 != score2) {
                return Integer.compare(score2, score1);
            }

            boolean disp1 = r1.tieneIngredientesSuficientes(despensa);
            boolean disp2 = r2.tieneIngredientesSuficientes(despensa);
            if (disp1 != disp2) {
                return disp1 ? -1 : 1;
            }

            return Integer.compare(r1.getTiempoPreparacion(), r2.getTiempoPreparacion());
        });

        return copia;
    }

    private int calcularPuntajeUrgencia(Receta receta, List<ProductoDespensa> despensa) {
        if (receta == null || receta.getIngredientes() == null || despensa == null) {
            return 0;
        }

        int score = 0;
        for (RecetaIngrediente ri : receta.getIngredientes()) {
            if (ri == null || ri.getIngrediente() == null) continue;
            int idIng = ri.getIngrediente().getId();

            for (ProductoDespensa prod : despensa) {
                if (prod != null && prod.getIngrediente() != null && prod.getIngrediente().getId() == idIng) {
                    if (prod.estaPorCaducar(3)) {
                        long dias = prod.diasParaCaducar();
                        if (dias >= 0 && dias <= 2) {
                            score += 250;
                        } else if (dias < 0) {
                            score += 180;
                        } else {
                            score += 120;
                        }
                    } else {
                        int urgencia = prod.calcularNivelUrgencia();
                        score += (urgencia * 10);
                    }
                }
            }
        }
        return score;
    }
}
