package modelo;

public class RecetaIngrediente {
    private Ingrediente ingrediente;
    private double cantidadNecesaria;

    public RecetaIngrediente() {
    }

    public RecetaIngrediente(Ingrediente ingrediente, double cantidadNecesaria) {
        this.ingrediente = ingrediente;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public double getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(double cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

    @Override
    public String toString() {
        return "RecetaIngrediente{" +
                "ingrediente=" + (ingrediente != null ? ingrediente.getNombre() : "null") +
                ", cantidadNecesaria=" + cantidadNecesaria +
                '}';
    }
}
