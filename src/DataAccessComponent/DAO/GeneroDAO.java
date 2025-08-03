package DataAccessComponent.DAO;

import DataAccessComponent.SQLiteDataHelper;
//imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para la gestión de géneros musicales en la base de datos.
 * Proporciona métodos para obtener información sobre los géneros musicales disponibles
 * en el sistema InkHarmony.
 * 
 * <p>Esta clase extiende SQLiteDataHelper para aprovechar la funcionalidad de conexión
 * a la base de datos y se especializa en el acceso a la tabla Genero que contiene
 * todos los géneros musicales disponibles en la aplicación.</p>
 * 
 * <p>Los métodos de esta clase son estáticos para facilitar su uso desde otras
 * partes del sistema sin necesidad de instanciar la clase.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class GeneroDAO extends SQLiteDataHelper {

    /**
     * Obtiene todos los géneros musicales disponibles en la base de datos.
     * 
     * <p>Este método recupera todos los registros de la tabla Genero y retorna
     * una lista con los nombres de todos los géneros musicales disponibles en
     * el sistema. Esta información es utilizada para validar las preferencias
     * musicales de los usuarios y para mostrar opciones en la interfaz.</p>
     * 
     * <p>Si ocurre un error durante la consulta, el método retorna una lista
     * vacía y registra el error en la consola.</p>
     * 
     * @return Una lista de strings con los nombres de todos los géneros musicales disponibles,
     *         o una lista vacía si ocurre un error o no hay géneros registrados
     * 
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     */
    public static List<String> obtenerTodos() {
        List<String> generos = new ArrayList<>();
        String sql = "SELECT nombre_genero FROM Genero";

        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                generos.add(rs.getString("nombre_genero"));
            }

        } catch (Exception e) {
            System.err.println("Error al obtener géneros desde la base de datos:");
            e.printStackTrace();
        }

        return generos;
    }
}