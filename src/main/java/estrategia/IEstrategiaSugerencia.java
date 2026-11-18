package estrategia;

import modelo.ProductoDespensa;
import modelo.Receta;

import java.util.List;

public interface IEstrategiaSugerencia {
    List<Receta> ordenarYFiltrar(List<Receta> recetas, List<ProductoDespensa> despensa);
}
