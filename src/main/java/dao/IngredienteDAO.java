package dao;

import modelo.Ingrediente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAO {

    public List<Ingrediente> listarTodos() {
        List<Ingrediente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, categoria, unidad_medida FROM ingredientes ORDER BY nombre ASC";
        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return lista;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingrediente ing = new Ingrediente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getString("unidad_medida")
                    );
                    lista.add(ing);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en IngredienteDAO.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    public Ingrediente buscarPorId(int id) {
        String sql = "SELECT id, nombre, categoria, unidad_medida FROM ingredientes WHERE id = ?";
        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Ingrediente(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getString("categoria"),
                                rs.getString("unidad_medida")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en IngredienteDAO.buscarPorId: " + e.getMessage());
        }
        return null;
    }
}
