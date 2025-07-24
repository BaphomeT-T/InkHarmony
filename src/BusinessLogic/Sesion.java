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
    private static PerfilDTO usuarioActual;
    
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
    public static void iniciarSesion(PerfilDTO usuarioLogeado) {
        usuarioActual = usuarioLogeado;
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
    public static void cerrarSesion() {
        usuarioActual = null;
    }
    
    /**
     * Verifica si hay un usuario autenticado en la sesión actual.
     * 
     * <p>Este método comprueba si existe un usuario activo en la sesión.
     * Es útil para determinar si el usuario debe autenticarse antes de
     * acceder a ciertas funcionalidades de la aplicación.</p>
     * 
     * @return true si hay un usuario autenticado, false en caso contrario
     * 
     * @throws RuntimeException Si ocurre un error durante la verificación del estado de autenticación
     */
    public static boolean estaAutenticado() {
        
        return usuarioActual != null;
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
    public static PerfilDTO obtenerUsuarioActual() {
        return usuarioActual;
    }
} 