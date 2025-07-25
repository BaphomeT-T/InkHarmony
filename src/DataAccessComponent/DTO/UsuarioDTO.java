package DataAccessComponent.DTO;

import java.util.Date;
import java.util.List;

/**
 * Clase DTO que representa un usuario en el sistema InkHarmony.
 * <p>
 * Extiende PerfilDTO e incluye la lista de preferencias musicales del usuario.
 * </p>
 *
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class UsuarioDTO extends PerfilDTO {
    /**
     * Lista de géneros musicales preferidos por el usuario.
     */
    private List<GeneroDTO> preferenciasMusicales;

    /**
     * Constructor por defecto de UsuarioDTO.
     * Crea una instancia vacía de UsuarioDTO.
     */
    public UsuarioDTO() {
        super();
    }

    /**
     * Constructor completo de UsuarioDTO con todos los parámetros.
     *
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param correo Correo electrónico del usuario
     * @param contrasenia Contraseña encriptada del usuario
     * @param tipoUsuario Tipo de usuario
     * @param fechaRegistro Fecha de registro
     * @param foto Foto de perfil
     * @param estadoCuenta Estado de la cuenta
     * @param preferenciasMusicales Lista de géneros musicales preferidos
     */
    public UsuarioDTO(String nombre, String apellido, String correo, String contrasenia, 
                   TipoUsuario tipoUsuario, Date fechaRegistro, String foto, 
                   String estadoCuenta, List<GeneroDTO> preferenciasMusicales) {
        super(nombre, apellido, correo, contrasenia, tipoUsuario, fechaRegistro, foto, estadoCuenta);
        this.preferenciasMusicales = preferenciasMusicales;
    }

    /**
     * Obtiene la lista de preferencias musicales del usuario.
     *
     * @return Lista de géneros musicales preferidos
     */
    public List<GeneroDTO> getPreferenciasMusicales() {
        return preferenciasMusicales;
    }

    /**
     * Establece la lista de preferencias musicales del usuario.
     *
     * @param preferenciasMusicales Lista de géneros musicales preferidos
     */
    public void setPreferenciasMusicales(List<GeneroDTO> preferenciasMusicales) {
        this.preferenciasMusicales = preferenciasMusicales;
    }
}