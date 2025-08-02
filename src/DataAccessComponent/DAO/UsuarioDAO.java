package DataAccessComponent.DAO;

import DataAccessComponent.DTO.GeneroDTO;
import DataAccessComponent.DTO.PerfilDTO;
import DataAccessComponent.SQLiteDataHelper;
// imports
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    public boolean guardarPreferencias(PerfilDTO perfil, List<GeneroDTO> generos) {
        String sql = "UPDATE Usuario SET preferencias_musicales = ? WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // Validar géneros contra la BD
            List<String> generosValidos = GeneroDAO.obtenerTodos();
            for (GeneroDTO genero : generos) {
                if (!generosValidos.contains(genero.getNombreGenero())) {
                    throw new SQLException("Género no registrado: " + genero.getNombreGenero());
                }
            }

            // Convertir a JSON y guardar
            pstmt.setString(1, BusinessLogic.Usuario.preferenciasToJSON(generos));
            pstmt.setString(2, perfil.getCorreo());
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
    public List<GeneroDTO> obtenerPreferencias(PerfilDTO perfil) {
        String sql = "SELECT preferencias_musicales FROM Usuario WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, perfil.getCorreo());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String json = rs.getString("preferencias_musicales");
                return BusinessLogic.Usuario.preferenciasFromJSON(json);
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("Error al obtener preferencias: " + e.getMessage());
            return List.of();
        }
    }


    public boolean actualizarPerfil(PerfilDTO perfil, boolean borrarPreferencias, List<GeneroDTO> nuevosGeneros) {
        StringBuilder sql = new StringBuilder("UPDATE Usuario SET ");
        List<Object> parametros = new ArrayList<>();

        List<GeneroDTO> preferenciasActuales = obtenerPreferencias(perfil);

        if (preferenciasActuales != null && nuevosGeneros != null) {
            Set<GeneroDTO> combinado = new LinkedHashSet<>(preferenciasActuales);
            combinado.addAll(nuevosGeneros);
            nuevosGeneros = new ArrayList<>(combinado);
        }

        if (perfil.getNombre() != null) {
            sql.append("nombre_usuario = ?, ");
            parametros.add(perfil.getNombre());
        }
        if (perfil.getApellido() != null) {
            sql.append("apellido_usuario = ?, ");
            parametros.add(perfil.getApellido());
        }
        if (perfil.getCorreo() != null) {
            sql.append("correo = ?, ");
            parametros.add(perfil.getCorreo());
        }
        if (perfil.getFoto() != null) {
            sql.append("id_foto_Perfil = ?, ");
            parametros.add(perfil.getFoto());
        }
        if (perfil.getContrasenia() != null) {
            sql.append("contraseña = ?, ");
            parametros.add(perfil.getContrasenia()); // Aquí ya debe estar encriptada
        }

        if (borrarPreferencias) {
            sql.append("preferencias_musicales = NULL, ");
        } else if (nuevosGeneros != null) {
            List<String> generosValidos = GeneroDAO.obtenerTodos();
            for (GeneroDTO genero : nuevosGeneros) {
                if (!generosValidos.contains(genero.getNombreGenero())) {
                    System.err.println("Género inválido: " + genero.getNombreGenero());
                    return false;
                }
            }

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < nuevosGeneros.size(); i++) {
                json.append("\"").append(nuevosGeneros.get(i).getNombreGenero()).append("\"");
                if (i < nuevosGeneros.size() - 1) json.append(",");
            }
            json.append("]");

            sql.append("preferencias_musicales = ?, ");
            parametros.add(BusinessLogic.Usuario.preferenciasToJSON(nuevosGeneros));
        }

        if (parametros.isEmpty() && !borrarPreferencias) {
            System.err.println("No se proporcionaron datos para actualizar.");
            return false;
        }

        // Quitar la última coma y espacio
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE correo = ?");
        parametros.add(perfil.getCorreo());  // Ojo: si quieres permitir cambiar el correo, ajusta este parámetro

        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            int filas = pstmt.executeUpdate();
            pstmt.close();
            return filas > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

}