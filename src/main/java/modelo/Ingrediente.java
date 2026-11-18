package modelo;

public class Ingrediente {
    private int id;
    private String nombre;
    private String categoria;
    private String unidadMedida;

    public Ingrediente() {
    }

    public Ingrediente(int id, String nombre, String categoria, String unidadMedida) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
    }

    public Ingrediente(String nombre, String categoria, String unidadMedida) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
    }

    public boolean validarDatos() {
        return nombre != null && !nombre.trim().isEmpty()
                && categoria != null && (categoria.equalsIgnoreCase("PERECEDERO") || categoria.equalsIgnoreCase("NO_PERECEDERO"))
                && unidadMedida != null && !unidadMedida.trim().isEmpty();
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    @Override
    public String toString() {
        return "Ingrediente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", unidadMedida='" + unidadMedida + '\'' +
                '}';
    }
}
