package DataAccessComponent.DAO;

import DataAccessComponent.DTO.Usuario;
import DataAccessComponent.SQLiteDataHelper;
import DataAccessComponent.DTO.Genero;
import java.sql.*;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para la gestión de preferencias musicales de usuarios.
 * Proporciona métodos para guardar, obtener y actualizar las preferencias musicales
 * de los usuarios en la base de datos SQLite.
 * 
 * <p>Esta clase extiende SQLiteDataHelper para aprovechar la funcionalidad de conexión
 * a la base de datos y se especializa en el manejo de preferencias musicales que se
 * almacenan como JSON en la base de datos.</p>
 * 
 * <p>Los métodos de esta clase incluyen validación de géneros musicales contra la
 * base de datos para asegurar la integridad de los datos.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class UsuarioDAO extends SQLiteDataHelper {

    /**
     * Guarda las preferencias musicales de un usuario en la base de datos.
     * 
     * <p>Este método actualiza las preferencias musicales de un usuario específico
     * en la tabla Usuario. Antes de guardar, valida que todos los géneros musicales
     * existan en la base de datos para mantener la integridad referencial.</p>
     * 
     * <p>Las preferencias se almacenan en formato JSON en la columna preferencias_musicales
     * de la tabla Usuario.</p>
     * 
     * @param correo El correo electrónico del usuario cuyas preferencias se van a guardar
     * @param generos La lista de géneros musicales que representan las preferencias del usuario
     * @return true si las preferencias se guardaron exitosamente, false en caso contrario
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el correo es null o está vacío, o si la lista de géneros es null
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public boolean guardarPreferencias(String correo, List<Genero> generos) {
        String sql = "UPDATE Usuario SET preferencias_musicales = ? WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // Validar géneros contra la BD
            List<String> generosValidos = GeneroDAO.obtenerTodos();
            for (Genero genero : generos) {
                if (!generosValidos.contains(genero.getNombreGenero())) {
                    throw new SQLException("Género no registrado: " + genero.getNombreGenero());
                }
            }

            // Convertir a JSON y guardar
            Usuario usuario = new Usuario();
            usuario.setPreferenciasMusicales(generos);
            pstmt.setString(1, usuario.toJSON());
            pstmt.setString(2, correo);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar preferencias: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene las preferencias musicales de un usuario desde la base de datos.
     * 
     * <p>Este método recupera las preferencias musicales almacenadas en formato JSON
     * para un usuario específico y las convierte en una lista de objetos Genero.
     * Si el usuario no tiene preferencias registradas, retorna una lista vacía.</p>
     * 
     * @param correo El correo electrónico del usuario cuyas preferencias se van a obtener
     * @return Una lista de objetos Genero que representan las preferencias musicales del usuario,
     *         o una lista vacía si no tiene preferencias registradas
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el correo es null o está vacío
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public List<Genero> obtenerPreferencias(String correo) {
        String sql = "SELECT preferencias_musicales FROM Usuario WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String json = rs.getString("preferencias_musicales");
                return Usuario.fromJSON(json);
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("Error al obtener preferencias: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Actualiza las preferencias musicales de un usuario en la base de datos.
     * 
     * <p>Este método reemplaza completamente las preferencias musicales existentes
     * de un usuario con una nueva lista de géneros. Si se proporciona null como
     * nuevos géneros, se eliminan todas las preferencias del usuario.</p>
     * 
     * <p>Antes de actualizar, valida que todos los géneros musicales existan en
     * la base de datos para mantener la integridad referencial.</p>
     * 
     * @param correo El correo electrónico del usuario cuyas preferencias se van a actualizar
     * @param nuevosGeneros La nueva lista de géneros musicales, o null para eliminar todas las preferencias
     * @return true si las preferencias se actualizaron exitosamente, false en caso contrario
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el correo es null o está vacío
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public boolean actualizarPreferencias(String correo, List<Genero> nuevosGeneros) {
        String sql = "UPDATE Usuario SET preferencias_musicales = ? WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            if (nuevosGeneros == null) {
                // Si es null, se actualiza la columna como NULL en la base de datos
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                // Validar géneros
                List<String> generosValidos = GeneroDAO.obtenerTodos();
                for (Genero genero : nuevosGeneros) {
                    if (!generosValidos.contains(genero.getNombreGenero())) {
                        return false;
                    }
                }

                // Convertir a JSON manualmente
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < nuevosGeneros.size(); i++) {
                    json.append("\"").append(nuevosGeneros.get(i).getNombreGenero()).append("\"");
                    if (i < nuevosGeneros.size() - 1) json.append(",");
                }
                json.append("]");
                pstmt.setString(1, json.toString());
            }

            pstmt.setString(2, correo);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar las preferencias: " + e.getMessage());
            return false;
        }
    }

}