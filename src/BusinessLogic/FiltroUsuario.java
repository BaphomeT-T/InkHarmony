package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.UsuarioDTO;
import DataAccessComponent.DTO.GeneroDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro decorador que restringe las recomendaciones a los géneros musicales preferidos por el usuario autenticado.
 * <p>
 * Utiliza la lista de preferencias musicales almacenadas en el perfil del usuario para filtrar las canciones recomendadas.
 * Este filtro debe colocarse en la cadena únicamente si el usuario ha iniciado sesión.
 * </p>
 *
 * @author Grupo F - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class FiltroUsuario extends FiltroRecomendador<CancionDTO> {
    
    /**
     * Conjunto de géneros preferidos por el usuario autenticado.
     */
    private Set<Genero> preferidos;

    /**
     * Crea una instancia del filtro por usuario, extrayendo las preferencias musicales desde su perfil.
     *
     * @param siguiente Componente decorado (puede ser otro filtro o un recomendador base)
     * @param usuario   Usuario autenticado cuyos géneros preferidos serán utilizados como criterio
     */
    public FiltroUsuario(Recomendador<CancionDTO> siguiente, UsuarioDTO usuario) {
        super(siguiente);
        this.preferidos = usuario.getPreferenciasMusicales().stream()
                .map(GeneroDTO::getNombreGenero)
                .map(Genero::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * Filtra las canciones de la lista recomendada para conservar solo aquellas que coincidan con
     * al menos uno de los géneros preferidos del usuario.
     *
     * @return Lista de canciones compatibles con las preferencias del usuario
     */
    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar().stream()
                .filter(c -> c.getGeneros() != null &&
                        c.getGeneros().stream().anyMatch(preferidos::contains))
                .collect(Collectors.toList());
    }
}
