package BusinessLogic;
//imports
import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.GeneroDTO;
import DataAccessComponent.DTO.PerfilDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase de lógica de negocio que gestiona las operaciones relacionadas con usuarios en InkHarmony.
 * <p>
 * Proporciona métodos para convertir preferencias musicales entre JSON y objetos,
 * así como para actualizar el perfil del usuario, incluyendo el manejo de la contraseña
 * y preferencias musicales.
 * </p>
 *
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class Usuario {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Convierte una lista de géneros musicales a una cadena JSON.
     *
     * @param preferenciasMusicales Lista de géneros musicales preferidos por el usuario
     * @return Cadena JSON que representa la lista de géneros
     */
    public static String preferenciasToJSON(List<GeneroDTO> preferenciasMusicales) {
        if (preferenciasMusicales == null || preferenciasMusicales.isEmpty()) {
            return "[]";
        }
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < preferenciasMusicales.size(); i++) {
            String nombreGenero = preferenciasMusicales.get(i).getNombreGenero().replace("\"", "\\\"");
            json.append("\"").append(nombreGenero).append("\"");
            if (i < preferenciasMusicales.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }


    /**
     * Convierte una cadena JSON de preferencias musicales a una lista de objetos GeneroDTO.
     *
     * @param json Cadena JSON que representa los géneros musicales
     * @return Lista de objetos GeneroDTO extraídos del JSON
     */
    public static List<GeneroDTO> preferenciasFromJSON(String json) {
        List<GeneroDTO> generos = new ArrayList<>();
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return generos;
        }
        String[] partes = json.substring(1, json.length() - 1).split(",");
        for (String parte : partes) {
            try {
            generos.add(new GeneroDTO(parte.trim().replace("\"", "")));
            } catch (IllegalArgumentException e) {
                // Género no válido en el enum, lo ignoramos o podrías registrar un warning
            }
        }
        return generos;
    }


    /**
     * Actualiza el perfil de un usuario, encriptando la contraseña si es necesario y gestionando preferencias musicales.
     *
     * @param perfil Objeto PerfilDTO con los datos a actualizar
     * @param borrarPreferencias Indica si se deben borrar las preferencias musicales
     * @param nuevosGeneros Lista de nuevos géneros musicales a asociar al usuario
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarPerfil(PerfilDTO perfil, boolean borrarPreferencias, List<GeneroDTO> nuevosGeneros) {
        // Solo encriptar si la contraseña *parece* estar en texto plano
        if (perfil.getContrasenia() != null && !perfil.getContrasenia().startsWith("$2a$")) {
            // Asumimos que si no empieza con "$2a$", no está encriptada
            String contraseniaEncriptada = encoder.encode(perfil.getContrasenia());
            perfil.setContrasenia(contraseniaEncriptada);
        }
        return usuarioDAO.actualizarPerfil(perfil, perfil.getCorreo(), borrarPreferencias, nuevosGeneros);
    }
} 