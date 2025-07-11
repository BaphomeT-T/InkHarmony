package BusinessLogic;

import DataAccessComponent.DAO.PerfilDAO;
import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DTO.TipoUsuario;
import DataAccessComponent.DTO.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.swing.*;
import java.util.Date;

public class ServicioPerfil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final PerfilDAO perfilDAO = new PerfilDAO();

    public void registrarUsuario(String nombre, String apellido, String correo, String contrasenia, String foto) {
        if(perfilDAO.buscarPorEmail(correo) != null) {
            System.out.println("El usuario ya está registrado");
            return;
        }
        String contraseniaEncriptada = encoder.encode(contrasenia);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEmail(correo);
        nuevoUsuario.setContrasenia(contraseniaEncriptada);
        nuevoUsuario.setFoto(foto);
        nuevoUsuario.setEstadoCuenta("activo");
        nuevoUsuario.setFechaRegistro(new Date());
        nuevoUsuario.setTipoUsuario(TipoUsuario.USUARIO);

        perfilDAO.guardar(nuevoUsuario);
    }
    public Perfil autenticar(String correo, String contraseniaIngresada) {
        Perfil perfil = perfilDAO.buscarPorEmail(correo);
        if (perfil != null && perfil.getEstado_cuenta().equalsIgnoreCase("activo")) {
            if(encoder.matches(contraseniaIngresada, perfil.getContrasenia())) {
                return perfil;
            }
        }
        return null;
    }
}
