package DataAccessComponent.DTO;

import java.util.List;

import DataAccessComponent.DAO.PerfilDAO;

public class Administrador extends Perfil {
    
    public Administrador() {
        super();
    }

    public Administrador(String nombre, String apellido, String email, String contrasenia, 
                        TipoUsuario tipoUsuario, java.util.Date fechaRegistro, String foto, String estadoCuenta) {
        super(nombre, apellido, email, contrasenia, tipoUsuario, fechaRegistro, foto, estadoCuenta);
    }
    public List<Perfil> consultarUsuarios() {
        PerfilDAO perfilDAO = new PerfilDAO();
        return perfilDAO.listarTodos();
    }

    public void activarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setEstadoCuenta("activo");
    }

    public void desactivarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setEstadoCuenta("desactivado");
    }

    public void cambiarTipoUsuario(Perfil usuario, String tipo) {
        // Implementación pendiente
        if ("ADMINISTRADOR".equalsIgnoreCase(tipo)) {
            usuario.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        } else if ("USUARIO".equalsIgnoreCase(tipo)) {
            usuario.setTipoUsuario(TipoUsuario.USUARIO);
        }
    }

    public void eliminarCuenta(Perfil usuario) {
        // Implementación pendiente
        // En una implementación real, esto podría marcar el usuario como eliminado
        // o realmente eliminarlo de la base de datos
    }

  
} 