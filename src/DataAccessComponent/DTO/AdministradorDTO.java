package DataAccessComponent.DTO;


/**
 * Clase DTO que representa un administrador del sistema InkHarmony.
 * Extiende la clase Perfil para heredar todas las propiedades de un usuario
 * y agrega funcionalidades específicas de administración.
 * 
 * <p>Esta clase proporciona métodos para gestionar usuarios del sistema,
 * incluyendo consultas, activación/desactivación de cuentas, cambio de tipos
 * de usuario y eliminación de cuentas. Los administradores tienen acceso
 * completo a las operaciones de gestión de usuarios.</p>
 * 
 * <p>La clase utiliza los DAOs correspondientes (PerfilDAO, UsuarioDAO) para
 * realizar las operaciones de base de datos de manera segura y consistente.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class AdministradorDTO extends PerfilDTO {
    
    /**
     * Constructor por defecto de Administrador.
     * Crea una instancia vacía de Administrador sin inicializar ningún campo.
     */
    public AdministradorDTO() {
        super();
    }
    
    /**
     * Constructor de Administrador que crea un administrador a partir de un perfil existente.
     * 
     * <p>Este constructor copia todos los datos del perfil proporcionado para crear
     * un nuevo administrador. Es útil para convertir un usuario regular en administrador.</p>
     * 
     * @param perfil El perfil de usuario que se va a convertir en administrador
     * 
     * @throws IllegalArgumentException Si el perfil es null
     */
    public AdministradorDTO(PerfilDTO perfil) {
        super(perfil.getNombre(), perfil.getApellido(), perfil.getCorreo(), perfil.getContrasenia(), perfil.getTipoUsuario(), perfil.getFechaRegistro(), perfil.getFoto(), perfil.getEstado_cuenta());
    }

    /**
     * Constructor completo de Administrador con todos los parámetros.
     * 
     * <p>Este constructor inicializa todos los campos del administrador con los valores
     * proporcionados. Es útil para crear administradores con datos completos.</p>
     * 
     * @param nombre El nombre del administrador
     * @param apellido El apellido del administrador
     * @param email La dirección de correo electrónico del administrador
     * @param contrasenia La contraseña encriptada del administrador
     * @param tipoUsuario El tipo de usuario (debe ser ADMINISTRADOR)
     * @param fechaRegistro La fecha en que se registró el administrador
     * @param foto La ruta o identificador de la foto de perfil
     * @param estadoCuenta El estado actual de la cuenta del administrador
     * 
     * @throws IllegalArgumentException Si algún parámetro requerido es null
     */
    public AdministradorDTO(String nombre, String apellido, String email, String contrasenia, 
                        TipoUsuario tipoUsuario, java.util.Date fechaRegistro, String foto, String estadoCuenta) {
        super(nombre, apellido, email, contrasenia, tipoUsuario, fechaRegistro, foto, estadoCuenta);
    }
    
  
} 