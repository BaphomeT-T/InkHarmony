package DataAccessComponent.DTO;

import java.util.List;

import BusinessLogic.ServicioPerfil;
import DataAccessComponent.DAO.PerfilDAO;

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
public class Administrador extends Perfil {
    
    /**
     * Constructor por defecto de Administrador.
     * Crea una instancia vacía de Administrador sin inicializar ningún campo.
     */
    public Administrador() {
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
    public Administrador(Perfil perfil) {
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
    public Administrador(String nombre, String apellido, String email, String contrasenia, 
                        TipoUsuario tipoUsuario, java.util.Date fechaRegistro, String foto, String estadoCuenta) {
        super(nombre, apellido, email, contrasenia, tipoUsuario, fechaRegistro, foto, estadoCuenta);
    }
    
    /**
     * Consulta todos los usuarios registrados en el sistema.
     * 
     * <p>Este método recupera una lista completa de todos los perfiles de usuario
     * registrados en la base de datos. Es útil para que los administradores puedan
     * revisar y gestionar los usuarios del sistema.</p>
     * 
     * @return Una lista de objetos Perfil con todos los usuarios registrados
     * 
     * @throws RuntimeException Si ocurre un error durante la consulta a la base de datos
     */
    public List<Perfil> consultarUsuarios() {
        PerfilDAO perfilDAO = new PerfilDAO();
        return perfilDAO.listarTodos();
    }

    /**
     * Activa la cuenta de un usuario del sistema.
     * 
     * <p>Este método cambia el estado de la cuenta de un usuario a "activo",
     * permitiéndole acceder nuevamente al sistema. La operación se persiste
     * inmediatamente en la base de datos.</p>
     * 
     * @param usuario El perfil del usuario cuya cuenta se va a activar
     * 
     * @throws IllegalArgumentException Si el usuario es null
     * @throws RuntimeException Si ocurre un error durante la actualización en la base de datos
     */
    public void activarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setEstadoCuenta("activo");
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar( usuario);
    }

    /**
     * Desactiva la cuenta de un usuario del sistema.
     * 
     * <p>Este método cambia el estado de la cuenta de un usuario a "desactivado",
     * impidiéndole acceder al sistema. La operación se persiste inmediatamente
     * en la base de datos.</p>
     * 
     * @param usuario El perfil del usuario cuya cuenta se va a desactivar
     * 
     * @throws IllegalArgumentException Si el usuario es null
     * @throws RuntimeException Si ocurre un error durante la actualización en la base de datos
     */
    public void desactivarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setEstadoCuenta("desactivado");
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar( usuario);
    }

    /**
     * Cambia el tipo de usuario de un perfil en el sistema.
     * 
     * <p>Este método actualiza el tipo de usuario de un perfil existente. Si el
     * nuevo tipo es ADMINISTRADOR, también elimina las preferencias musicales
     * del usuario ya que los administradores no necesitan estas preferencias.</p>
     * 
     * @param usuario El perfil del usuario cuyo tipo se va a cambiar
     * @param tipoUsuario El nuevo tipo de usuario a asignar
     * 
     * @throws IllegalArgumentException Si el usuario es null o el tipoUsuario es null
     * @throws RuntimeException Si ocurre un error durante la actualización en la base de datos
     */
    public void cambiarTipoUsuario(Perfil usuario, TipoUsuario tipoUsuario) {
        usuario.setTipoUsuario(tipoUsuario);
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar(usuario);
        ServicioPerfil servicio = new ServicioPerfil();
        if (tipoUsuario == TipoUsuario.ADMINISTRADOR) {
            servicio.actualizarPerfil(usuario, true, null);
        }
    }

    /**
     * Elimina permanentemente la cuenta de un usuario del sistema.
     * 
     * <p>Este método elimina completamente el registro del usuario de la base de datos.
     * Esta operación es irreversible y debe usarse con precaución.</p>
     * 
     * @param usuario El perfil del usuario cuya cuenta se va a eliminar
     * 
     * @throws IllegalArgumentException Si el usuario es null
     * @throws RuntimeException Si ocurre un error durante la eliminación en la base de datos
     */
    public void eliminarCuenta(Perfil usuario) {
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.eliminar(usuario);
    }

  
} 