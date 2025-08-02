/*
|-------------------------------------|
| © 202 EPN-FIS, All rights reserved |
| GR1SW                              |
|-------------------------------------|
Autor: Jonathan Pagual,Juan Cofre, Jhordy Parra
Descripcion: Data Helper para SQLite
*/

package DataAccessComponent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase abstracta que proporciona funcionalidades de conexión a la base de datos SQLite.
 * 
 * <p>Esta clase actúa como un helper para la gestión de conexiones a la base de datos
 * SQLite utilizada por el sistema InkHarmony. Proporciona métodos para abrir y cerrar
 * conexiones de manera segura y sincronizada.</p>
 * 
 * <p>La clase implementa un patrón Singleton para la conexión, asegurando que solo
 * exista una instancia de conexión activa en todo momento. Los métodos son estáticos
 * y sincronizados para garantizar thread-safety en entornos multi-hilo.</p>
 * 
 * <p>Esta clase es la base para todas las clases DAO del sistema, proporcionándoles
 * la funcionalidad de conexión a la base de datos de manera transparente.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 27-02-2024
 */
public abstract class SQLiteDataHelper {
    
    /** Ruta de conexión a la base de datos SQLite */
    private static String DBPathConnection = "jdbc:sqlite:database\\InkHarmony.sqlite";

    /** Instancia única de la conexión a la base de datos */
    private static Connection conn = null;

    /**
     * Constructor protegido de SQLiteDataHelper.
     * 
     * <p>Este constructor está marcado como protegido para permitir que las clases
     * que extiendan SQLiteDataHelper puedan instanciarla, pero no permite la
     * creación directa de instancias desde fuera del paquete.</p>
     */
    protected SQLiteDataHelper() {}

    /**
     * Abre una conexión a la base de datos SQLite de manera sincronizada.
     * 
     * <p>Este método implementa un patrón Singleton para la conexión a la base de datos.
     * Si no existe una conexión activa, crea una nueva. Si ya existe una conexión,
     * retorna la conexión existente. El método es sincronizado para garantizar
     * thread-safety en entornos multi-hilo.</p>
     * 
     * <p>La conexión se establece utilizando el driver JDBC de SQLite y la ruta
     * de conexión configurada en la variable DBPathConnection.</p>
     * 
     * @return La conexión a la base de datos SQLite
     * 
     * @throws SQLException Si ocurre un error durante la conexión a la base de datos
     * @throws Exception Si ocurre un error general durante el proceso de conexión
     */
    protected static synchronized Connection openConnection() throws Exception {
        try {
            if (conn == null)
                conn = DriverManager.getConnection(DBPathConnection);
        } catch (SQLException e) {
            throw e;
        }
        return conn;
    }

    /**
     * Cierra la conexión activa a la base de datos SQLite.
     * 
     * <p>Este método cierra la conexión actual a la base de datos si existe.
     * Después de cerrar la conexión, la variable conn se establece en null,
     * permitiendo que se cree una nueva conexión en futuras llamadas a openConnection().</p>
     * 
     * <p>Es importante cerrar las conexiones cuando ya no se necesiten para
     * liberar recursos del sistema y evitar problemas de memoria.</p>
     * 
     * @throws SQLException Si ocurre un error durante el cierre de la conexión
     * @throws Exception Si ocurre un error general durante el proceso de cierre
     */
    protected static void closeConnection() throws Exception {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            throw e;
        }
    }
}
