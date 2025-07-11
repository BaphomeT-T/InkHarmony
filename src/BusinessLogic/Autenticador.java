package BusinessLogic;

import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DAO.PerfilDAO;

public class Autenticador {
    
    private PerfilDAO perfilDAO;
    
    public Autenticador() {
        this.perfilDAO = new PerfilDAO();
    }
    
    public boolean validarCredenciales(String emailPorVerificar, String contraseniaPorVerificar) {
        // Implementación pendiente
        Perfil perfil = perfilDAO.buscarPorCorreo(emailPorVerificar);
        if (perfil != null && perfil.getEstado_cuenta().equals("activo")) {
            return perfil.getContrasenia().equals(contraseniaPorVerificar);
        }
        return false;
    }
    
    public Perfil obtenerPerfil(String email) {
        // Implementación pendiente
        return perfilDAO.buscarPorCorreo(email);
    }
} 