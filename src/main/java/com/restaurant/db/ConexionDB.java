package com.restaurant.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ============================================================
 *  PATRÓN DE DISEÑO: SINGLETON  (Etapa 3)
 * ============================================================
 *  Gestiona una única instancia de conexión a la base de datos
 *  durante todo el ciclo de vida de la aplicación.
 *
 *  Implementación Thread-Safe con "Double-Checked Locking"
 *  para entornos web multi-hilo (Apache Tomcat).
 *
 *  Uso:
 *      Connection conn = ConexionDB.getInstance().getConnection();
 * ============================================================
 */
public class ConexionDB {

    // ----------------------------------------------------------------
    //  Configuración de la base de datos – AJUSTA SEGÚN TU ENTORNO
    // ----------------------------------------------------------------
    private static final String URL      = "jdbc:mysql://localhost:3306/restaurant_inventario"
                                         + "?useSSL=false&serverTimezone=America/Lima"
                                         + "&characterEncoding=UTF-8";
    private static final String USUARIO  = "root";       // ← cambia por tu usuario MySQL
    private static final String PASSWORD = "dairon1626";            // ← cambia por tu contraseña MySQL
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";

    // ----------------------------------------------------------------
    //  Instancia única (volatile garantiza visibilidad entre hilos)
    // ----------------------------------------------------------------
    private static volatile ConexionDB instancia = null;

    // Conexión JDBC reutilizable
    private Connection connection = null;

    // ----------------------------------------------------------------
    //  Constructor privado: nadie puede instanciar esta clase desde fuera
    // ----------------------------------------------------------------
    private ConexionDB() {
        try {
            Class.forName(DRIVER);
            System.out.println("[ConexionDB] Driver MySQL cargado correctamente.");
        } catch (ClassNotFoundException e) {
            System.err.println("[ConexionDB] ERROR: Driver no encontrado – " + e.getMessage());
            throw new RuntimeException("No se pudo cargar el driver MySQL.", e);
        }
    }

    // ----------------------------------------------------------------
    //  Método de acceso global a la instancia única (Singleton)
    //  Double-Checked Locking para thread-safety
    // ----------------------------------------------------------------
    public static ConexionDB getInstance() {
        if (instancia == null) {
            synchronized (ConexionDB.class) {
                if (instancia == null) {
                    instancia = new ConexionDB();
                    System.out.println("[ConexionDB] Instancia Singleton creada.");
                }
            }
        }
        return instancia;
    }

    // ----------------------------------------------------------------
    //  Obtiene (o restablece si está cerrada) la conexión JDBC
    // ----------------------------------------------------------------
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[ConexionDB] Conexión establecida con la base de datos.");
        }
        return connection;
    }

    // ----------------------------------------------------------------
    //  Cierra la conexión de forma segura
    // ----------------------------------------------------------------
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[ConexionDB] Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("[ConexionDB] Error al cerrar conexión: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    // ----------------------------------------------------------------
    //  Evita clonación (rompe el patrón Singleton)
    // ----------------------------------------------------------------
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("El Singleton no puede ser clonado.");
    }
}
