package BusinessLogic;

import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DAO.PerfilDAO;

public class Sesion {
    private Perfil usuarioActual;
    private PerfilDAO perfilDAO;
    
    public Sesion() {
        this.perfilDAO = new PerfilDAO();
    }
    
    public boolean iniciarSesion(String email, String contrasenia) {
        // Implementación pendiente
        Perfil perfil = perfilDAO.buscarPorEmail(email);
        if (perfil != null && perfil.isCuentaActiva() && 
            perfil.getContrasenia().equals(contrasenia)) {
            this.usuarioActual = perfil;
            return true;
        }
        return false;
    }
    
    public void cerrarSesion() {
        // Implementación pendiente
        this.usuarioActual = null;
    }
    
    public boolean estaAutenticado() {
        // Implementación pendiente
        return usuarioActual != null;
    }
    
    public Perfil obtenerUsuarioActual() {
        // Implementación pendiente
        return usuarioActual;
    }
} 