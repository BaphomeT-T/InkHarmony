package BusinessLogic;

import DataAccessComponent.DTO.PerfilDTO;
import DataAccessComponent.DTO.TipoUsuario;
import DataAccessComponent.DAO.PerfilDAO;
import java.util.List;

/**
 * Clase de lógica de negocio que gestiona las operaciones administrativas sobre los usuarios en InkHarmony.
 * <p>
 * Permite consultar, activar, desactivar, cambiar el tipo y eliminar cuentas de usuario.
 * </p>
 *
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class Administrador {
    /**
     * Consulta todos los usuarios registrados en el sistema.
     *
     * @return Lista de perfiles de usuario
     */
    public List<PerfilDTO> consultarUsuarios() {
        PerfilDAO perfilDAO = new PerfilDAO();
        return perfilDAO.listarTodos();
    }

    /**
     * Activa la cuenta de un usuario.
     *
     * @param usuario PerfilDTO del usuario a activar
     */
    public void activarCuenta(PerfilDTO usuario) {
        usuario.setEstadoCuenta("activo");
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar(usuario);
    }

    /**
     * Desactiva la cuenta de un usuario.
     *
     * @param usuario PerfilDTO del usuario a desactivar
     */
    public void desactivarCuenta(PerfilDTO usuario) {
        usuario.setEstadoCuenta("desactivado");
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar(usuario);
    }

    /**
     * Cambia el tipo de usuario de un perfil.
     *
     * @param usuario PerfilDTO del usuario
     * @param tipoUsuario Nuevo tipo de usuario
     */
    public void cambiarTipoUsuario(PerfilDTO usuario, TipoUsuario tipoUsuario) {
        usuario.setTipoUsuario(tipoUsuario);
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.actualizar(usuario);
        Usuario usuarioBL = new Usuario();
        if (tipoUsuario == TipoUsuario.ADMINISTRADOR) {
            usuarioBL.actualizarPerfil(usuario, true, null);
        }
    }

    /**
     * Elimina la cuenta de un usuario.
     *
     * @param usuario PerfilDTO del usuario a eliminar
     */
    public void eliminarCuenta(PerfilDTO usuario) {
        PerfilDAO perfilDAO = new PerfilDAO();
        perfilDAO.eliminar(usuario);
    }
} 