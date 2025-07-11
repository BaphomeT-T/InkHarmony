package BusinessLogic;

import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DAO.PerfilDAO;

public class Sesion {
    private static Perfil usuarioActual;
    
    public static void iniciarSesion(Perfil usuarioLogeado) {
        usuarioActual = usuarioLogeado;
    }
    
    public static void cerrarSesion() {
        // Implementación pendiente
        usuarioActual = null;
    }
    
    public static boolean estaAutenticado() {
        // Implementación pendiente
        return usuarioActual != null;
    }
    
    public static Perfil obtenerUsuarioActual() {
        // Implementación pendiente
        return usuarioActual;
    }
} 