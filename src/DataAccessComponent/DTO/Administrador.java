package DataAccessComponent.DTO;

import java.util.List;

import DataAccessComponent.DAO.PerfilDAO;

public class Administrador extends Perfil {
    
    public Administrador() {
        super();
    }
    public Administrador(Perfil perfil) {
        super(perfil.getNombre(), perfil.getApellido(), perfil.getCorreo(), perfil.getContrasenia(), perfil.getTipoUsuario(), perfil.getFechaRegistro(), perfil.getFoto(), perfil.getEstado_cuenta());
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
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar( usuario);
    }

    public void desactivarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setEstadoCuenta("desactivado");
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar( usuario);
    }

    public void cambiarTipoUsuario(Perfil usuario, TipoUsuario tipoUsuario) {
        usuario.setTipoUsuario(tipoUsuario);
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar( usuario);
    }

    public void eliminarCuenta(Perfil usuario) {
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.eliminar(usuario);
    }

  
} 