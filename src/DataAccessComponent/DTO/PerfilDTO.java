package DataAccessComponent.DTO;

import java.util.Date;

/**
 * Clase DTO (Data Transfer Object) que representa un perfil de usuario en el sistema InkHarmony.
 * Contiene toda la información personal y de cuenta de un usuario registrado en la aplicación.
 * 
 * <p>Esta clase encapsula los datos de un usuario incluyendo información personal (nombre,
 * apellido, correo), credenciales de acceso (contraseña), configuración de cuenta (tipo
 * de usuario, estado de cuenta), y metadatos (fecha de registro, foto de perfil).</p>
 * 
 * <p>La clase proporciona constructores para crear perfiles y métodos getter/setter
 * para acceder y modificar los datos del usuario de manera controlada.</p>
 * 
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class PerfilDTO {
    /** Nombre del usuario */
    private String nombre;
    
    /** Apellido del usuario */
    private String apellido;
    
    /** Dirección de correo electrónico del usuario (identificador único) */
    private String correo;
    
    /** Contraseña encriptada del usuario */
    private String contrasenia;
    
    /** Tipo de usuario (USUARIO, ADMINISTRADOR, etc.) */
    private TipoUsuario tipoUsuario;
    
    /** Fecha en que se registró el usuario en el sistema */
    private Date fechaRegistro;
    
    /** Ruta o identificador de la foto de perfil del usuario */
    private String foto;
    
    /** Estado actual de la cuenta del usuario (activo, inactivo, suspendido) */
    private String estadoCuenta;

    /**
     * Constructor por defecto de Perfil.
     * Crea una instancia vacía de Perfil sin inicializar ningún campo.
     */
    public PerfilDTO() {}

    /**
     * Constructor completo de Perfil con todos los parámetros.
     * 
     * <p>Este constructor inicializa todos los campos del perfil con los valores
     * proporcionados. Es útil para crear objetos Perfil con datos completos
     * desde la base de datos o para crear nuevos usuarios.</p>
     * 
     * @param nombre El nombre del usuario
     * @param apellido El apellido del usuario
     * @param correo La dirección de correo electrónico del usuario
     * @param contrasenia La contraseña encriptada del usuario
     * @param tipoUsuario El tipo de usuario (USUARIO, ADMINISTRADOR, etc.)
     * @param fechaRegistro La fecha en que se registró el usuario
     * @param foto La ruta o identificador de la foto de perfil
     * @param estadoCuenta El estado actual de la cuenta del usuario
     * 
     * @throws IllegalArgumentException Si algún parámetro requerido es null
     */
    public PerfilDTO(String nombre, String apellido, String correo, String contrasenia, TipoUsuario tipoUsuario, Date fechaRegistro, String foto , String estadoCuenta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.tipoUsuario = tipoUsuario;
        this.fechaRegistro = fechaRegistro;
        this.foto = foto;
        this.estadoCuenta = estadoCuenta;
    }

    // Getters y setters
    
    /**
     * Obtiene el nombre del usuario.
     * 
     * @return El nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * 
     * @param nombre El nuevo nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario.
     * 
     * @return El apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario.
     * 
     * @param apellido El nuevo apellido del usuario
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene la dirección de correo electrónico del usuario.
     * 
     * @return La dirección de correo electrónico del usuario
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece la dirección de correo electrónico del usuario.
     * 
     * @param correo La nueva dirección de correo electrónico del usuario
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la contraseña encriptada del usuario.
     * 
     * @return La contraseña encriptada del usuario
     */
    public String getContrasenia() {
        return contrasenia;
    }

    /**
     * Establece la contraseña encriptada del usuario.
     * 
     * @param contrasenia La nueva contraseña encriptada del usuario
     */
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    /**
     * Obtiene el estado actual de la cuenta del usuario.
     * 
     * @return El estado actual de la cuenta del usuario
     */
    public String getEstado_cuenta() {
        return estadoCuenta;
    }

    /**
     * Establece el estado de la cuenta del usuario.
     * 
     * @param estadoCuenta El nuevo estado de la cuenta del usuario
     */
    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    /**
     * Obtiene el tipo de usuario.
     * 
     * @return El tipo de usuario (USUARIO, ADMINISTRADOR, etc.)
     */
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    /**
     * Establece el tipo de usuario.
     * 
     * @param tipoUsuario El nuevo tipo de usuario
     */
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Obtiene la fecha de registro del usuario.
     * 
     * @return La fecha en que se registró el usuario en el sistema
     */
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro del usuario.
     * 
     * @param fechaRegistro La nueva fecha de registro del usuario
     */
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Obtiene la ruta o identificador de la foto de perfil del usuario.
     * 
     * @return La ruta o identificador de la foto de perfil
     */
    public String getFoto() {
        return foto;
    }

    /**
     * Establece la ruta o identificador de la foto de perfil del usuario.
     * 
     * @param foto La nueva ruta o identificador de la foto de perfil
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }
} 