package dao;

import modelo.Ingrediente;
import modelo.ProductoDespensa;
import modelo.ProductoNoPerecedero;
import modelo.ProductoPerecedero;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DespensaDAO {

    public List<ProductoDespensa> listarTodoConDetalle() {
        List<ProductoDespensa> lista = new ArrayList<>();
        String sql = "SELECT d.id, d.id_ingrediente, d.cantidad, d.fecha_caducidad, " +
                     "       i.nombre, i.categoria, i.unidad_medida " +
                     "FROM despensa d " +
                     "INNER JOIN ingredientes i ON d.id_ingrediente = i.id " +
                     "ORDER BY CASE WHEN d.fecha_caducidad IS NULL THEN 1 ELSE 0 END, " +
                     "         d.fecha_caducidad ASC, d.id ASC";

        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return lista;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int idIng = rs.getInt("id_ingrediente");
                    double cantidad = rs.getDouble("cantidad");
                    Date sqlDate = rs.getDate("fecha_caducidad");
                    LocalDate fechaCad = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                    String nombre = rs.getString("nombre");
                    String categoria = rs.getString("categoria");
                    String unidadMedida = rs.getString("unidad_medida");

                    Ingrediente ingrediente = new Ingrediente(idIng, nombre, categoria, unidadMedida);
                    ProductoDespensa producto;

                    if ("PERECEDERO".equalsIgnoreCase(categoria)) {
                        producto = new ProductoPerecedero(id, ingrediente, cantidad, fechaCad);
                    } else {
                        producto = new ProductoNoPerecedero(id, ingrediente, cantidad, fechaCad);
                    }
                    lista.add(producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DespensaDAO.listarTodoConDetalle: " + e.getMessage());
        }
        return lista;
    }

    public void insertar(int idIngrediente, double cantidad, LocalDate fecha) {
        String sql = "INSERT INTO despensa (id_ingrediente, cantidad, fecha_caducidad) VALUES (?, ?, ?)";
        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idIngrediente);
                ps.setDouble(2, cantidad);
                if (fecha == null) {
                    ps.setNull(3, Types.DATE);
                } else {
                    ps.setDate(3, Date.valueOf(fecha));
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error en DespensaDAO.insertar: " + e.getMessage());
        }
    }

    public void actualizarCantidad(int id, double cantidad) {
        if (cantidad <= 0) {
            eliminar(id);
            return;
        }
        String sql = "UPDATE despensa SET cantidad = ? WHERE id = ?";
        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, cantidad);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error en DespensaDAO.actualizarCantidad: " + e.getMessage());
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM despensa WHERE id = ?";
        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error en DespensaDAO.eliminar: " + e.getMessage());
        }
    }

    public void descontarFIFO(int idIngrediente, double cantidadUsada) {
        if (cantidadUsada <= 0) return;

        String selectSql = "SELECT id, cantidad FROM despensa " +
                           "WHERE id_ingrediente = ? " +
                           "ORDER BY CASE WHEN fecha_caducidad IS NULL THEN 1 ELSE 0 END, " +
                           "         fecha_caducidad ASC, id ASC";

        try {
            Connection conn = ConexionBD.getInstancia().getConexion();
            if (conn == null) return;

            boolean autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                List<int[]> lotesParaActualizar = new ArrayList<>();
                List<Double> cantidadesRestantes = new ArrayList<>();
                List<Integer> lotesParaEliminar = new ArrayList<>();

                double pendiente = cantidadUsada;

                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setInt(1, idIngrediente);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next() && pendiente > 0.000001) {
                            int loteId = rs.getInt("id");
                            double loteCantidad = rs.getDouble("cantidad");

                            if (loteCantidad <= pendiente) {
                                lotesParaEliminar.add(loteId);
                                pendiente -= loteCantidad;
                            } else {
                                double nuevoValor = loteCantidad - pendiente;
                                lotesParaActualizar.add(new int[]{loteId});
                                cantidadesRestantes.add(nuevoValor);
                                pendiente = 0.0;
                            }
                        }
                    }
                }

                if (!lotesParaEliminar.isEmpty()) {
                    String deleteSql = "DELETE FROM despensa WHERE id = ?";
                    try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                        for (int idEliminar : lotesParaEliminar) {
                            psDel.setInt(1, idEliminar);
                            psDel.executeUpdate();
                        }
                    }
                }

                if (!lotesParaActualizar.isEmpty()) {
                    String updateSql = "UPDATE despensa SET cantidad = ? WHERE id = ?";
                    try (PreparedStatement psUpd = conn.prepareStatement(updateSql)) {
                        for (int i = 0; i < lotesParaActualizar.size(); i++) {
                            psUpd.setDouble(1, cantidadesRestantes.get(i));
                            psUpd.setInt(2, lotesParaActualizar.get(i)[0]);
                            psUpd.executeUpdate();
                        }
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(autoCommitOriginal);
            }

        } catch (SQLException e) {
            System.err.println("Error en DespensaDAO.descontarFIFO: " + e.getMessage());
        }
    }
}
