package DataAccessComponent.DTO;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Usuario extends Perfil {
    private List<Genero> preferenciasMusicales;

    // Constructores (¡mantenidos!)
    public Usuario() {
        super();
    }

    public Usuario(String nombre, String apellido, String correo, String contrasenia, 
                   TipoUsuario tipoUsuario, Date fechaRegistro, String foto, 
                   String estadoCuenta, List<Genero> preferenciasMusicales) {
        super(nombre, apellido, correo, contrasenia, tipoUsuario, fechaRegistro, foto, estadoCuenta);
        this.preferenciasMusicales = preferenciasMusicales;
    }

    // Método para convertir List<Genero> a JSON (manual)
    public String toJSON() {
        if (preferenciasMusicales == null || preferenciasMusicales.isEmpty()) {
            return "[]";
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < preferenciasMusicales.size(); i++) {
            String nombreGenero = preferenciasMusicales.get(i).getNombreGenero()
                                    .replace("\"", "\\\"");
            json.append("\"").append(nombreGenero).append("\"");
            if (i < preferenciasMusicales.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    // Método para parsear JSON a List<Genero> (manual)
    public static List<Genero> fromJSON(String json) {
        List<Genero> generos = new ArrayList<>();
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return generos;
        }
        String[] partes = json.substring(1, json.length() - 1).split(",");
        for (String parte : partes) {
            generos.add(new Genero(parte.trim().replace("\"", "")));
        }
        return generos;
    }

    // Getters y Setters
    public List<Genero> getPreferenciasMusicales() {
        return preferenciasMusicales;
    }

    public void setPreferenciasMusicales(List<Genero> preferenciasMusicales) {
        this.preferenciasMusicales = preferenciasMusicales;
    }
}