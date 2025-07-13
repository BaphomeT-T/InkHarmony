package DataAccessComponent.DTO;

/**
 * Enumeración que define los tipos de usuario disponibles en el sistema InkHarmony.
 * 
 * <p>Esta enumeración establece los roles de usuario que pueden tener las cuentas
 * en la aplicación. Cada tipo de usuario tiene diferentes permisos y funcionalidades
 * disponibles dentro del sistema.</p>
 * 
 * <p>Los tipos de usuario se utilizan para controlar el acceso a diferentes
 * funcionalidades de la aplicación y para determinar qué operaciones puede realizar
 * cada usuario.</p>
 * 
 * @author InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public enum TipoUsuario {
    /**
     * Tipo de usuario administrador.
     * 
     * <p>Los administradores tienen acceso completo al sistema, incluyendo
     * funcionalidades de gestión de usuarios, configuración del sistema y
     * todas las operaciones disponibles para usuarios regulares.</p>
     * 
     * <p>Los administradores pueden:</p>
     * <ul>
     *   <li>Gestionar cuentas de usuario (activar, desactivar, eliminar)</li>
     *   <li>Cambiar tipos de usuario</li>
     *   <li>Consultar todos los usuarios del sistema</li>
     *   <li>Acceder a todas las funcionalidades de usuario regular</li>
     * </ul>
     */
    ADMINISTRADOR,
    
    /**
     * Tipo de usuario regular.
     * 
     * <p>Los usuarios regulares tienen acceso a las funcionalidades básicas
     * de la aplicación, incluyendo la gestión de su propio perfil y
     * preferencias musicales.</p>
     * 
     * <p>Los usuarios regulares pueden:</p>
     * <ul>
     *   <li>Gestionar su propio perfil</li>
     *   <li>Configurar preferencias musicales</li>
     *   <li>Acceder a funcionalidades de reproducción de música</li>
     *   <li>Realizar búsquedas y explorar contenido</li>
     * </ul>
     */
    USUARIO
} 