package BusinessLogic;

import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Filtro que recomienda canciones basadas en las preferencias musicales del usuario actual.
 * 
 * <p>Este filtro utiliza los géneros musicales que el usuario ha marcado como preferidos
 * para limitar los resultados de recomendación. Si el usuario no tiene preferencias o no hay
 * coincidencias, se continúa con el siguiente filtro de la cadena.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public class FiltroPreferencias extends FiltroRecomendador {

    /** Conjunto de géneros preferidos por el usuario. */
    private final Set<Genero> preferidos;

    /**
     * Construye el filtro de preferencias utilizando al usuario actualmente en sesión.
     * 
     * @param siguiente el recomendador al que se delega en caso de que no se apliquen preferencias
     * @throws IllegalStateException si no hay un usuario autenticado en sesión
     * @throws NullPointerException si el filtro siguiente es nulo
     */
    public FiltroPreferencias(Recomendador siguiente) {
        super(Objects.requireNonNull(siguiente, "El filtro siguiente no puede ser null"));

        PerfilDTO perfil = Sesion.getSesion().obtenerUsuarioActual();
        if (perfil == null) {
            throw new IllegalStateException(
                "No hay usuario en sesión: no se pueden cargar preferencias");
        }

        List<GeneroDTO> generosDTO;
        if (perfil instanceof UsuarioDTO u && u.getPreferenciasMusicales() != null)
            generosDTO = u.getPreferenciasMusicales();
        else
            generosDTO = new UsuarioDAO().obtenerPreferencias(perfil);

        this.preferidos = generosDTO.stream()
                                    .map(GeneroDTO::getNombreGenero)
                                    .map(Genero::valueOf)
                                    .collect(Collectors.toSet());
    }

    /**
     * Devuelve una lista de canciones que coinciden con los géneros musicales preferidos del usuario.
     * Si no hay preferencias registradas, se delega la recomendación al siguiente filtro.
     *
     * @return lista filtrada de canciones recomendadas según las preferencias del usuario
     */
    @Override
    public List<CancionDTO> recomendar() {
        if (preferidos.isEmpty())
            return siguiente.recomendar();
        return siguiente.recomendar().stream()
                       .filter(c -> c.getGeneros() != null &&
                                    c.getGeneros().stream().anyMatch(preferidos::contains))
                       .collect(Collectors.toList());
    }
}
