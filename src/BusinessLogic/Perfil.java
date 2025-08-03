package BusinessLogic;

// Stub de perfil
class Perfil {
    private String nombre;
    private String correo;

    public Perfil(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
}
