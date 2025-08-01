package BusinessLogic;

import DataAccessComponent.DAO.PerfilDAO;
import DataAccessComponent.DTO.PerfilDTO;
import DataAccessComponent.DTO.TipoUsuario;
import DataAccessComponent.DTO.UsuarioDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

/**
 * Clase que maneja la lógica de negocio para la gestión de perfiles de usuario.
 * Proporciona funcionalidades para el registro de nuevos usuarios y la autenticación
 * de usuarios existentes, incluyendo el encriptado seguro de contraseñas.
 * 
 * <p>Esta clase utiliza BCrypt para el encriptado de contraseñas y se comunica
 * con la capa de acceso a datos a través de PerfilDAO para persistir y recuperar
 * información de usuarios.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class ServicioPerfil {
    /** Codificador BCrypt para el encriptado seguro de contraseñas */
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /** Objeto DAO para el acceso a datos de perfiles */
    private final PerfilDAO perfilDAO = new PerfilDAO();



    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * <p>Este método realiza las siguientes operaciones:</p>
     * <ul>
     *   <li>Verifica que el correo electrónico no esté ya registrado</li>
     *   <li>Encripta la contraseña usando BCrypt</li>
     *   <li>Crea un nuevo objeto Usuario con los datos proporcionados</li>
     *   <li>Establece valores por defecto (estado activo, fecha de registro, tipo usuario)</li>
     *   <li>Persiste el usuario en la base de datos</li>
     * </ul>
     * 
     * @param nombre El nombre del usuario
     * @param apellido El apellido del usuario
     * @param correo El correo electrónico del usuario (debe ser único)
     * @param contrasenia La contraseña en texto plano (será encriptada)
     * @param foto La ruta o identificador de la foto de perfil
     * 
     * @throws IllegalArgumentException Si alguno de los parámetros es null o vacío
     * @throws RuntimeException Si ocurre un error durante el proceso de registro
     */
    public void registrarUsuario(String nombre, String apellido, String correo, String contrasenia, String foto) {
        if(perfilDAO.buscarPorEmail(correo) != null) {
            System.out.println("El usuario ya está registrado");
            return;
        }
        String contraseniaEncriptada = encoder.encode(contrasenia);

        UsuarioDTO nuevoUsuario = new UsuarioDTO();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasenia(contraseniaEncriptada);
        nuevoUsuario.setFoto(foto);
        nuevoUsuario.setEstadoCuenta("activo");
        nuevoUsuario.setFechaRegistro(new Date());
        nuevoUsuario.setTipoUsuario(TipoUsuario.USUARIO);

        perfilDAO.guardar(nuevoUsuario);
    }
    
    /**
     * Autentica un usuario utilizando su correo electrónico y contraseña.
     * 
     * <p>Este método realiza la autenticación siguiendo estos pasos:</p>
     * <ol>
     *   <li>Busca el usuario por su correo electrónico</li>
     *   <li>Verifica que la cuenta esté activa</li>
     *   <li>Compara la contraseña ingresada con la contraseña encriptada almacenada</li>
     * </ol>
     * 
     * <p>Si la autenticación es exitosa, retorna el perfil completo del usuario.
     * Si falla, retorna null.</p>
     * 
     * @param correo El correo electrónico del usuario
     * @param contraseniaIngresada La contraseña en texto plano ingresada por el usuario
     * @return El objeto Perfil del usuario si la autenticación es exitosa, null en caso contrario
     * 
     * @throws IllegalArgumentException Si el correo o contraseña son null
     * @throws RuntimeException Si ocurre un error durante el proceso de autenticación
     */
    public PerfilDTO autenticar(String correo, String contraseniaIngresada) {
        PerfilDTO perfil = perfilDAO.buscarPorEmail(correo);
        if (perfil != null && perfil.getEstado_cuenta().equalsIgnoreCase("activo")) {
            if(encoder.matches(contraseniaIngresada, perfil.getContrasenia())) {
                return perfil;
            }
        }
        return null;
    }
}