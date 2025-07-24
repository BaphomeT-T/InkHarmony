package BusinessLogic;

import DataAccessComponent.DTO.PerfilDTO;

/**
 * Clase que gestiona la sesión del usuario actual en el sistema.
 * Proporciona funcionalidades para iniciar, cerrar y verificar el estado
 * de la sesión de un usuario autenticado.
 * 
 * <p>Esta clase utiliza un patrón Singleton para mantener una única instancia
 * del usuario actual durante toda la sesión de la aplicación. La información
 * del usuario se almacena en memoria y se mantiene hasta que se cierre la sesión.</p>
 * 
 * <p>La clase es thread-safe ya que utiliza variables estáticas para el almacenamiento
 * de la información de sesión.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class Sesion {
    /** Perfil del usuario actualmente autenticado en el sistema */
    private PerfilDTO usuarioActual;
    /** Instancia única de Sesion*/
    private static Sesion sesion;

    /**
     * Constructor privado para evitar la creación de múltiples instancias
     */
    private Sesion() {
    }

    /**
     * Devuelve la instancia única de la clase {@code Sesion}.
     *
     * <p>Si la instancia aún no ha sido creada, se crea en ese momento </p>
     *
     * @return la instancia única de {@code Sesion}.
     */
    public static Sesion getSesion() {
        if(sesion == null) {
            sesion = new Sesion();
        } else {
            System.out.println("Ya existe una sesión activa");
            return sesion;
        }
        return sesion;
    }

    /**
     * Inicia la sesión para un usuario autenticado.
     * 
     * <p>Este método establece el usuario proporcionado como el usuario actual
     * de la sesión. Una vez llamada esta función, el usuario estará disponible
     * en toda la aplicación hasta que se cierre la sesión.</p>
     * 
     * @param usuarioLogeado El perfil del usuario que se va a establecer como usuario actual
     * 
     * @throws IllegalArgumentException Si el usuarioLogeado es null
     * @throws RuntimeException Si ocurre un error durante el proceso de inicio de sesión
     */
    public void iniciarSesion(PerfilDTO usuarioLogeado) {
        if (usuarioLogeado == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.usuarioActual = usuarioLogeado;
    }
    
    /**
     * Cierra la sesión del usuario actual.
     * 
     * <p>Este método limpia la información del usuario actual de la sesión,
     * efectivamente cerrando la sesión del usuario. Después de llamar este método,
     * el usuario deberá autenticarse nuevamente para acceder a funcionalidades
     * que requieran autenticación.</p>
     * 
     * <p>Si no hay un usuario autenticado, este método no tiene efecto.</p>
     * 
     * @throws RuntimeException Si ocurre un error durante el proceso de cierre de sesión
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    
    /**
     * Obtiene el perfil del usuario actualmente autenticado.
     * 
     * <p>Este método retorna el objeto Perfil del usuario que está
     * actualmente autenticado en el sistema. Si no hay usuario autenticado,
     * retorna null.</p>
     * 
     * <p>Es importante verificar que el usuario esté autenticado antes de
     * llamar este método para evitar valores null inesperados.</p>
     * 
     * @return El objeto Perfil del usuario actual, o null si no hay usuario autenticado
     * 
     * @throws RuntimeException Si ocurre un error durante la obtención del usuario actual
     */
    public PerfilDTO obtenerUsuarioActual() {
        return this.usuarioActual;
    }
} 