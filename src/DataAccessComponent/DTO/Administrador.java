package DataAccessComponent.DTO;

import java.util.List;

public class Administrador extends Perfil {
    
    public Administrador() {
        super();
    }

    public Administrador(String nombre, String apellido, String email, String contrasenia, 
                        TipoUsuario tipoUsuario, java.util.Date fechaRegistro, String foto) {
        super(nombre, apellido, email, contrasenia, tipoUsuario, fechaRegistro, foto);
    }

    public void activarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setCuentaActiva(true);
    }

    public void desactivarCuenta(Perfil usuario) {
        // Implementación pendiente
        usuario.setCuentaActiva(false);
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

    public List<Perfil> consultarUsuarios() {
        // Implementación pendiente
        // Este método debería obtener la lista de usuarios desde el DAO
        return null;
    }
} 