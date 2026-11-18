package estrategia;

import modelo.ProductoDespensa;
import modelo.Receta;

import java.util.ArrayList;
import java.util.List;

public class EstrategiaDisponibilidadTotal implements IEstrategiaSugerencia {

    @Override
    public List<Receta> ordenarYFiltrar(List<Receta> recetas, List<ProductoDespensa> despensa) {
        if (recetas == null) {
            return new ArrayList<>();
        }

        List<Receta> copia = new ArrayList<>(recetas);

        copia.sort((r1, r2) -> {
            boolean suf1 = r1.tieneIngredientesSuficientes(despensa);
            boolean suf2 = r2.tieneIngredientesSuficientes(despensa);

            if (suf1 != suf2) {
                return suf1 ? -1 : 1;
            }

            int faltan1 = r1.ingredientesFaltantes(despensa).size();
            int faltan2 = r2.ingredientesFaltantes(despensa).size();
            if (faltan1 != faltan2) {
                return Integer.compare(faltan1, faltan2);
            }

            return Integer.compare(r1.getTiempoPreparacion(), r2.getTiempoPreparacion());
        });

        return copia;
    }
}
