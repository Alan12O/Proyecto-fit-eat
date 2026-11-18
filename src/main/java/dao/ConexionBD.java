package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/despensa_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "240712";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error cargando driver MySQL: " + e.getMessage());
        }
    }

    private ConexionBD() {
        conectar();
    }

    private void conectar() {
        try {
            String url = System.getProperty("DB_URL", System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DEFAULT_URL);
            String user = System.getProperty("DB_USER", System.getenv("DB_USER") != null ? System.getenv("DB_USER") : DEFAULT_USER);
            String pass = System.getProperty("DB_PASSWORD", System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : DEFAULT_PASSWORD);

            this.conexion = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("Error conectando a MySQL: " + e.getMessage());
            this.conexion = null;
        }
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public synchronized Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            System.err.println("Error verificando conexión MySQL: " + e.getMessage());
            conectar();
        }
        return conexion;
    }
}
