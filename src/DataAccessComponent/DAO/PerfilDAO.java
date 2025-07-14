package DataAccessComponent.DAO;

import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DTO.TipoUsuario;
import DataAccessComponent.SQLiteDataHelper;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Clase DAO (Data Access Object) para la gestión de perfiles de usuario en la base de datos.
 * Proporciona métodos para realizar operaciones CRUD (Create, Read, Update, Delete)
 * sobre la tabla Usuario en la base de datos SQLite.
 * 
 * <p>Esta clase extiende SQLiteDataHelper para aprovechar la funcionalidad de conexión
 * a la base de datos y proporciona una interfaz de alto nivel para el acceso a datos
 * de perfiles de usuario.</p>
 * 
 * <p>Los métodos de esta clase manejan automáticamente la apertura y cierre de conexiones,
 * así como el manejo de excepciones SQL.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class PerfilDAO extends SQLiteDataHelper {

    /**
     * Constructor por defecto de PerfilDAO.
     * Inicializa el objeto DAO sin parámetros específicos.
     */
    public PerfilDAO() {

    }

    /**
     * Guarda un nuevo perfil de usuario en la base de datos.
     * 
     * <p>Este método inserta un nuevo registro en la tabla Usuario con los datos
     * del perfil proporcionado. El estado de la cuenta se establece automáticamente
     * como "activo" durante el proceso de inserción.</p>
     * 
     * @param perfil El objeto Perfil que contiene los datos del usuario a guardar
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el perfil es null o contiene datos inválidos
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public void guardar(Perfil perfil) {
        String sql = """
                    INSERT INTO Usuario (
                                 nombre_usuario ,
                                 apellido_usuario ,
                                 correo ,
                                 contraseña ,
                                 id_foto_Perfil ,
                                 estado_cuenta ,
                                 tipo_usuario)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, perfil.getNombre());
            pstmt.setString(2, perfil.getApellido());
            pstmt.setString(3, perfil.getCorreo());
            pstmt.setString(4, perfil.getContrasenia());
            pstmt.setString(5, perfil.getFoto());
            pstmt.setString(6, "activo");
            pstmt.setString(7, perfil.getTipoUsuario().toString());

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca un perfil de usuario por su dirección de correo electrónico.
     * 
     * <p>Este método realiza una consulta en la tabla Usuario para encontrar
     * un registro que coincida con el correo electrónico proporcionado. Si se
     * encuentra un usuario, se crea y retorna un objeto Perfil con todos sus datos.</p>
     * 
     * @param correo La dirección de correo electrónico del usuario a buscar
     * @return El objeto Perfil del usuario si se encuentra, null en caso contrario
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el correo es null o está vacío
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public Perfil buscarPorEmail(String correo) {
        String sql = "SELECT * FROM Usuario WHERE correo = ?";

        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Perfil perfil = crearPerfilDesdeResultSet(rs);
                rs.close();
                pstmt.close();
                return perfil;
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene una lista de todos los perfiles de usuario en la base de datos.
     * 
     * <p>Este método recupera todos los registros de la tabla Usuario y los
     * convierte en objetos Perfil. La lista puede estar vacía si no hay
     * usuarios registrados en la base de datos.</p>
     * 
     * @return Una lista de objetos Perfil con todos los usuarios registrados
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public List<Perfil> listarTodos() {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";

        try {
            Connection conn = openConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                perfiles.add(crearPerfilDesdeResultSet(rs));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perfiles;
    }

    /**
     * Elimina un perfil de usuario de la base de datos.
     * 
     * <p>Este método elimina permanentemente el registro del usuario de la tabla
     * Usuario basándose en su dirección de correo electrónico. Esta operación
     * es irreversible.</p>
     * 
     * @param perfil El objeto Perfil del usuario que se va a eliminar
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el perfil es null o no tiene correo válido
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public void eliminar(Perfil perfil) {
        String sql = "DELETE FROM Usuario WHERE Correo = ?";

        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, perfil.getCorreo());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza los datos de un perfil de usuario existente.
     * 
     * <p>Este método actualiza el estado de la cuenta y el tipo de usuario
     * de un perfil existente en la base de datos. La búsqueda se realiza
     * por la dirección de correo electrónico del usuario.</p>
     * 
     * @param perfil El objeto Perfil con los datos actualizados
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el perfil es null o no tiene correo válido
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public void actualizar(Perfil perfil) {
        String sql = """
                    UPDATE Usuario
                    SET  
                    estado_cuenta= ?,
                    tipo_usuario= ?
                    WHERE correo = ?
                """;

        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, perfil.getEstado_cuenta());
            pstmt.setString(2, perfil.getTipoUsuario().toString());
            pstmt.setString(3, perfil.getCorreo());

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Desactiva la cuenta de un usuario.
     * 
     * <p>Este método marca la cuenta de un usuario como inactiva estableciendo
     * el campo cuenta_activa en 0. El usuario permanece en la base de datos
     * pero no puede acceder al sistema.</p>
     * 
     * @param perfil El objeto Perfil del usuario que se va a desactivar
     * 
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta SQL
     * @throws IllegalArgumentException Si el perfil es null o no tiene correo válido
     * @throws RuntimeException Si ocurre un error de conexión con la base de datos
     */
    public void desactivar(Perfil perfil) {
        String sql = "UPDATE Usuario SET cuenta_activa = 0 WHERE Correo = ?";

        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, perfil.getCorreo());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un objeto Perfil a partir de un ResultSet de la base de datos.
     * 
     * <p>Este método privado convierte los datos de un ResultSet en un objeto
     * Perfil, manejando la conversión de tipos de datos y el formateo de fechas.
     * Se utiliza internamente por otros métodos de la clase.</p>
     * 
     * @param rs El ResultSet que contiene los datos del usuario
     * @return Un objeto Perfil creado a partir de los datos del ResultSet
     * 
     * @throws SQLException Si ocurre un error al leer los datos del ResultSet
     * @throws IllegalArgumentException Si el ResultSet es null
     */
    private Perfil crearPerfilDesdeResultSet(ResultSet rs) throws SQLException {

        Perfil perfil = new Perfil();
        perfil.setNombre(rs.getString(2));
        perfil.setApellido(rs.getString(3));
        perfil.setCorreo(rs.getString(4));
        perfil.setContrasenia(rs.getString(5));
        perfil.setEstadoCuenta(rs.getString(8));
        perfil.setTipoUsuario(TipoUsuario.valueOf(rs.getString(10)));
        Timestamp timestamp = rs.getTimestamp(6);
        Date fechaRegistro = null;

        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = sdf.format(new Date(timestamp.getTime()));
            // System.out.println(fechaFormateada);
            try {
                fechaRegistro = sdf.parse(fechaFormateada);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                fechaRegistro = new Date(timestamp.getTime());
            }
            perfil.setFechaRegistro(fechaRegistro);
        }

        perfil.setFoto(rs.getString(7));
        return perfil;
    }

}