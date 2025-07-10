package DataAccessComponent.DTO;

import java.util.List;

public class Usuario extends Perfil {
    private List<Generos> preferenciasMusicales;

    public Usuario() {
        super();
    }

    public Usuario(String nombre, String apellido, String email, String contrasenia, 
                   TipoUsuario tipoUsuario, java.util.Date fechaRegistro, String foto, String estadoCuenta, List<Generos> preferenciasMusicales) {
        super(nombre, apellido, email, contrasenia, tipoUsuario, fechaRegistro, foto, estadoCuenta);
        this.preferenciasMusicales = preferenciasMusicales;
    }

    public void registrarUsuario(String nombre, String apellido, String email, String contrasenia) {
        // Implementación pendiente
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setEmail(email);
        this.setContrasenia(contrasenia);
    }

    public void actualizarPerfil(String nombre, String apellido, String foto) {
        // Implementación pendiente
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setFoto(foto);
    }

    public void agregarPreferenciasMusicales(List<Generos> generos) {
        // Implementación pendiente
        this.preferenciasMusicales = generos;
    }

    // Getters y setters
    public List<Generos> getPreferenciasMusicales() {
        return preferenciasMusicales;
    }

    public void setPreferenciasMusicales(List<Generos> preferenciasMusicales) {
        this.preferenciasMusicales = preferenciasMusicales;
    }
} 